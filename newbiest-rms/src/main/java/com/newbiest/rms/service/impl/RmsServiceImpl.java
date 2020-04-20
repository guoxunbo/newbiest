package com.newbiest.rms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DefaultStatusMachine;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.context.model.Context;
import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.ContextValueRepository;
import com.newbiest.context.service.ContextService;
import com.newbiest.rms.RmsConfiguration;
import com.newbiest.rms.exception.RmsException;
import com.newbiest.rms.model.*;
import com.newbiest.rms.repository.*;
import com.newbiest.rms.service.RmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author guoxunbo
 * @date 2020-04-05 11:16
 */
@Service
@Transactional
@Slf4j
@BaseJpaFilter
public class RmsServiceImpl implements RmsService {

    @Autowired
    RmsConfiguration rmsConfiguration;

    @Autowired
    private RecipeEquipmentRepository recipeEquipmentRepository;

    @Autowired
    private RecipeEquipmentParameterRepository recipeEquipmentParameterRepository;

    @Autowired
    private RecipeEquipmentHisRepository recipeEquipmentHisRepository;

    @Autowired
    private RecipeEquipmentUnitRepository recipeEquipmentUnitRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private RecipeEquipmentParameterTempRepository recipeEquipmentParameterTempRepository;

    @Autowired
    private ContextService contextService;

    @Autowired
    private ContextValueRepository contextValueRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private VersionControlService versionControlService;

    /**
     * 创建recipeEquipment 如果存在以前的版本，则做升版本，不然就直接保存第一版本
     * @param recipeEquipment
     * @return
     * @throws ClientException
     */
    public RecipeEquipment createRecipeEquipment(RecipeEquipment recipeEquipment) throws ClientException {
        try {
            String equipmentId = recipeEquipment.getEquipmentId();
            Equipment equipment = equipmentRepository.getByEquipmentId(equipmentId);
            if (equipment == null) {
                throw new ClientParameterException(RmsException.EQP_IS_NOT_EXIST, equipmentId);
            }
            recipeEquipment.setStatus(DefaultStatusMachine.STATUS_UNFROZEN);
            recipeEquipment.setPattern(StringUtils.isNullOrEmpty(recipeEquipment.getPattern()) ? RecipeEquipment.PATTERN_NORMAL : recipeEquipment.getPattern());
            recipeEquipment.setEquipmentType(equipment.getEquipmentType());

            // 取得最高版本
            List<RecipeEquipment> allRecipeEquipments = recipeEquipmentRepository.getByNameAndEquipmentIdAndPatternOrderByVersionDesc(recipeEquipment.getName(), recipeEquipment.getEquipmentId(), recipeEquipment.getPattern());
            Map<String, RecipeEquipmentParameter> parameterMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(allRecipeEquipments)) {
                RecipeEquipment lastRecipeEquipment = allRecipeEquipments.get(0);
                recipeEquipment.setVersion(lastRecipeEquipment.getVersion() + 1);

                // 取得激活版本或者没有存在激活的就取最高版本参数，parameter的compareFlag/SpecialFlag以及range值都从原有版本上来
                Optional<RecipeEquipment> optional = allRecipeEquipments.stream().filter(temp -> DefaultStatusMachine.STATUS_ACTIVE.equals(temp.getStatus())).findFirst();
                if (optional.isPresent()) {
                    RecipeEquipment activeRecipeEquipment = optional.get();
                    activeRecipeEquipment = getRecipeEquipmentParameter(activeRecipeEquipment);
                    parameterMap = activeRecipeEquipment.getRecipeEquipmentParameters().stream().collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));
                } else {
                    lastRecipeEquipment = getRecipeEquipmentParameter(lastRecipeEquipment);
                    parameterMap = lastRecipeEquipment.getRecipeEquipmentParameters().stream().collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));
                }
            } else {
                recipeEquipment.setVersion(1L);
            }
            List<RecipeEquipmentParameter> recipeEquipmentParameters = recipeEquipment.getRecipeEquipmentParameters();
            recipeEquipment = (RecipeEquipment) baseService.saveEntity(recipeEquipment);
            // 保存parameter 如果是升级从原有版本上拿到是否比较,是否特殊以及range范围
            for (RecipeEquipmentParameter parameter : recipeEquipmentParameters) {
                if (parameterMap.containsKey(parameter.getFullName())) {
                    RecipeEquipmentParameter checkParameter = parameterMap.get(parameter.getFullName());
                    parameter.setCompareFlag(checkParameter.getCompareFlag());
                    parameter.setSpecialParameterFlag(checkParameter.getSpecialParameterFlag());
                    parameter.setRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
                    //TODO 此处暂时无法确定EAP上报能否上报range相关。
//                    if (RecipeEquipmentParameter.VALIDATE_TYPE_RANGE.equals(parameter.getValidateType()) && RecipeEquipmentParameter.VALIDATE_TYPE_RANGE.equals(checkParameter.getValidateType())) {
//                        if (StringUtils.isNullOrEmpty(parameter.getMinValue()) && !StringUtils.isNullOrEmpty(checkParameter.getMinValue())) {
//                            double minValue = Double.parseDouble(parameter.getMinValue());
//                            double checkMinValue = Double.parseDouble(checkParameter.getMinValue());
//                            parameter.setMinValue(String.valueOf(minValue < checkMinValue ? minValue : checkMinValue));
//                        }
//                        if (StringUtils.isNullOrEmpty(parameter.getMaxValue()) && !StringUtils.isNullOrEmpty(checkParameter.getMaxValue())) {
//                            double maxValue = Double.parseDouble(parameter.getMaxValue());
//                            double checkMaxValue = Double.parseDouble(checkParameter.getMaxValue());
//                            parameter.setMinValue(String.valueOf(maxValue > checkMaxValue ? maxValue : checkMaxValue));
//                        }
//                    }
                }
                recipeEquipmentParameterRepository.save(parameter);
            }
            return recipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public RecipeEquipment getRecipeEquipmentParameter(RecipeEquipment recipeEquipment) {
        try {
            List<RecipeEquipmentParameter> parameters = recipeEquipmentParameterRepository.findByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
            if (CollectionUtils.isEmpty(parameters)) {
                parameters = Lists.newArrayList();
            }
            recipeEquipment.setRecipeEquipmentParameters(parameters);
            return recipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public void deleteRecipeEquipment(Long recipeEquipmentRrn) throws ClientException {
        try {
            RecipeEquipment recipeEquipment = recipeEquipmentRepository.findByObjectRrn(recipeEquipmentRrn);
            if (!(DefaultStatusMachine.STATUS_UNFROZEN.equals(recipeEquipment.getStatus()))) {
                throw new ClientException(RmsException.EQP_RECIPE_DELETE_ONLY_UNFROZEN);
            }
            List<RecipeEquipmentUnit> recipeEquipmentUnits = recipeEquipmentUnitRepository.findByUnitRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
            if (CollectionUtils.isNotEmpty(recipeEquipmentUnits)) {
                throw new ClientException(RmsException.EQP_RECIPE_ALREADY_USED_BY_OTHERS);
            }

            List<RecipeEquipment> subRecipeEquipments = recipeEquipmentRepository.getByParentRrn(recipeEquipment.getObjectRrn());
            for (RecipeEquipment subRecipeEquipment : subRecipeEquipments) {
                deleteRecipeEquipment(subRecipeEquipment.getObjectRrn());
            }
            recipeEquipmentParameterRepository.deleteByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());

            // 删除所属的子Recipe关系
            recipeEquipmentUnitRepository.deleteByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());

            baseService.delete(recipeEquipment);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取当前激活版本的设备Recipe.如果没找到，则根据findGolden决定是否要去寻找GoldenRecipe
     * @param name recipe名称
     * @param equipmentId 设备号
     * @param pattern 模式
     * @param findGolden 是否查找GoldenRecipe
     * @return
     * @throws ClientException
     */
    public RecipeEquipment getActiveRecipeEquipment(String name, String equipmentId, String pattern, boolean findGolden) throws ClientException {
        try {
            RecipeEquipment activeRecipeEquipment = recipeEquipmentRepository.getByNameAndEquipmentIdAndPatternAndStatus(name, equipmentId, pattern, DefaultStatusMachine.STATUS_ACTIVE);
            if (activeRecipeEquipment != null) {
                return activeRecipeEquipment;
            } else {
                if (findGolden) {
                    RecipeEquipment goldenRecipeEquipment = getGoldenRecipeEquipment(name, equipmentId, pattern);
                    return goldenRecipeEquipment;
                }
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取设备类型的goldenRecipe。
     * @param name recipe 名称
     * @param equipmentId 设备号
     * @param pattern 模式
     * @return
     * @throws ClientException
     */
    public RecipeEquipment getGoldenRecipeEquipment(String name, String equipmentId, String pattern) throws ClientException {
        try {
            Equipment equipment = equipmentRepository.getByEquipmentId(equipmentId);
            if (equipment == null) {
                throw new ClientParameterException(RmsException.EQP_IS_NOT_EXIST, equipmentId);
            }
            RecipeEquipment goldenRecipeEquipment = recipeEquipmentRepository.getByNameAndEquipmentTypeAndPatternAndStatusAndGoldenFlag(name, equipment.getEquipmentType(), pattern, DefaultStatusMachine.STATUS_ACTIVE, StringUtils.YES);
            return goldenRecipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public RecipeEquipment activeRecipeEquipment(RecipeEquipment recipeEquipment) throws ClientException {
        try {
            RecipeEquipment activeRecipeEquipment = getActiveRecipeEquipment(recipeEquipment.getName(), recipeEquipment.getEquipmentId(), recipeEquipment.getPattern(), false);
            if (activeRecipeEquipment != null) {
                activeRecipeEquipment = (RecipeEquipment) versionControlService.inactive(activeRecipeEquipment);
            }
            DefaultStatusMachine.trigger(recipeEquipment, DefaultStatusMachine.EVENT_ACTIVE);
            recipeEquipment = recipeEquipmentRepository.saveAndFlush(recipeEquipment);
            recipeEquipment.setEffectObject(activeRecipeEquipment);

            baseService.saveHistoryEntity(recipeEquipment, DefaultStatusMachine.EVENT_ACTIVE);

            if (rmsConfiguration.isSendNotification()) {
                Map<String, Object> notificationMap = Maps.newHashMap();
                // TODO 处理激活通知
//                notificationMap.put(SendRmsTransContext.KEY_ACTIVE_TYPE, recipeEquipment.getActiveType());
//                sendNotification(recipeEquipment.getName(), recipeEquipment.getEquipmentId(), recipeEquipment.getEquipmentType(), recipeEquipment.getPattern(), NotificationRequest.NOTIFICATION_TYPE_ACTIVE, notificationMap, sc);
            }
            return recipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public void setGoldenRecipe(RecipeEquipment recipeEquipment) throws ClientException {
        try {
            if (recipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST);
            }
            Equipment equipment = equipmentRepository.getByEquipmentId(recipeEquipment.getEquipmentId());
            if (equipment == null) {
                throw new ClientException(RmsException.EQP_IS_NOT_EXIST);
            }
            if (DefaultStatusMachine.STATUS_ACTIVE.equals(recipeEquipment.getStatus())) {
                RecipeEquipment recipeEqp = recipeEquipmentRepository.getGoldenRecipe(ThreadLocalContext.getOrgRrn(), equipment.getEquipmentType(), recipeEquipment.getName(), DefaultStatusMachine.STATUS_ACTIVE, recipeEquipment.getPattern(), false);

                if (recipeEqp != null) {
                    throw new ClientException(RmsException.EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST);
                }
                // 设置成GoldenRecipe则需要清空EquipmentId
                recipeEquipment.setEquipmentId(null);
                recipeEquipment.setEquipmentType(equipment.getEquipmentType());
                recipeEquipment.setGoldenFlag(true);
                recipeEquipment = recipeEquipmentRepository.save(recipeEquipment);

                baseService.saveHistoryEntity(recipeEquipment, RecipeEquipmentHis.TRANS_TYPE_SET_GOLDEN);
            } else {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_ACTIVE);
            }
        } catch (ClientException e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public void unSetGoldenRecipe(RecipeEquipment recipeEquipment, String equipmentId) throws ClientException {
        try {
            if (!recipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_GOLDEN);
            }
            if (!DefaultStatusMachine.STATUS_ACTIVE.equals(recipeEquipment.getStatus())) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_ACTIVE);
            }
            // TODO unset的时候是否卡控必输equipmentId
            if (!StringUtils.isNullOrEmpty(equipmentId)) {
                Equipment equipment = equipmentRepository.getByEquipmentId(equipmentId);
                if (equipment == null) {
                    throw new ClientException(RmsException.EQP_IS_NOT_EXIST);
                }
                recipeEquipment.setEquipmentId(equipment.getEquipmentId());
            }

            recipeEquipment.setGoldenFlag(false);
            recipeEquipment = recipeEquipmentRepository.save(recipeEquipment);
            baseService.saveHistoryEntity(recipeEquipment, RecipeEquipmentHis.TRANS_TYPE_UNSET_GOLDEN);
        } catch (ClientException e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void checkRecipeEquipmentBody(List<RecipeEquipment> recipeEquipmentList, List<String> tempValues, boolean checkHoldState)  throws ClientException{
        try {
            for (RecipeEquipment checkRecipeEquipment : recipeEquipmentList) {
                checkRecipeEquipmentBody(checkRecipeEquipment, tempValues, checkHoldState);
            }
        } catch (ClientException e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 检查Recipe一旦有一个不通过就立马抛出异常
     * @param checkRecipeEquipment 待检查的recipeEquipment
     * @param tempValues 临时值 比如当前的lot 当前的wafer等会去查找相应的RecipeParameterTemp
     * @param checkHoldState 是否检查Hold
     * @throws ClientException
     */
    public void checkRecipeEquipmentBody(RecipeEquipment checkRecipeEquipment, List<String> tempValues, boolean checkHoldState)  throws ClientException{
        try {
            RecipeEquipment recipeEquipment = getActiveRecipeEquipment(checkRecipeEquipment.getName(), checkRecipeEquipment.getEquipmentId(), checkRecipeEquipment.getPattern(), true);
            if (recipeEquipment == null) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_EXIST);
            }

            if (recipeEquipment.getCheckBodyFlag()) {
                if (!StringUtils.isNullOrEmpty(recipeEquipment.getBody())) {
                    if (!recipeEquipment.getBody().equals(checkRecipeEquipment.getBody())) {
                        throw new ClientException(RmsException.EQP_RECIPE_BODY_NOT_SAME);
                    }
                } else {
                    throw new ClientException(RmsException.EQP_RECIPE_BODY_IS_NOT_EXIST);
                }
            }

//            if (recipeEquipment.getTimestampFlag()) {
//                if (recipeEquipment.getTimestamp() != null) {
//                    long time = recipeEquipment.getTimestamp().getTime();
//                    long checkTime = checkRecipeEquipment.getTimestamp().getTime();
//                    if (time != checkTime) {
//                        throw new ClientException(RmsException.EQP_RECIPE_TIMESTAMP_NOT_SAME);
//                    }
//                } else {
//                    throw new ClientException(RmsException.EQP_RECIPE_TIMESTAMP_IS_NOT_EXIST);
//                }
//            }

            if (recipeEquipment.getCheckSumFlag()) {
                if (!StringUtils.isNullOrEmpty(recipeEquipment.getCheckSum())) {
                    if (!recipeEquipment.getCheckSum().equals(checkRecipeEquipment.getCheckSum())) {
                        throw new ClientException(RmsException.EQP_RECIPE_CHECKSUM_NOT_SAME);
                    }
                } else {
                    throw new ClientException(RmsException.EQP_RECIPE_CHECKSUM_IS_NOT_EXIST);
                }
            }

            if (checkHoldState) {
                if (RecipeEquipment.HOLD_STATE_ON.equals(recipeEquipment.getHoldState())) {
                    throw new ClientException(RmsException.EQP_RECIPE_STATE_ON_HOLD);
                }
            }

            if (recipeEquipment.getCheckParameterFlag()) {
                if (CollectionUtils.isNotEmpty(tempValues)) {
                    // 根据临时参数找到相应的临时修改参数
                    List<RecipeEquipmentParameterTemp> tempParameters = getOnlineRecipe(recipeEquipment, tempValues);
                    if (CollectionUtils.isNotEmpty(tempParameters)) {
                        List<RecipeEquipmentParameter> checkRecipeParameters = checkRecipeEquipment.getRecipeEquipmentParameters();
                        for (RecipeEquipmentParameter parameter : checkRecipeParameters) {
                            Optional<RecipeEquipmentParameterTemp> optional = tempParameters.stream().filter(temp -> temp.getParameterName().equals(parameter.getParameterName()) && temp.getParameterGroup().equals(parameter.getParameterGroup())).findFirst();
                            if (optional.isPresent()) {
                                RecipeEquipmentParameterTemp temp = optional.get();
                                parameter.setCurrentValue(temp.getParameterValue());
                                // 计数
                                temp.setCurrentCount(temp.getCurrentCount() + 1);
                                temp.changeStatus();
                                recipeEquipmentParameterTempRepository.save(temp);
                            }
                        }
                        checkRecipeEquipment.setRecipeEquipmentParameters(checkRecipeParameters);
                    }
                }
            }

            // 只组合需要比较的即compareFlag是true的
            Map<String, RecipeEquipmentParameter> parameterMap = recipeEquipment.getRecipeEquipmentParameters()
                    .stream().filter(recipeEquipmentParameter -> recipeEquipmentParameter.getCompareFlag()).collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));

            Map<String, RecipeEquipmentParameter> checkParameterMap = checkRecipeEquipment.getRecipeEquipmentParameters()
                    .stream().collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));

            // 校验必须要compare的parameter是否在check列表中
            for (String parameterName : parameterMap.keySet()) {
                if (!checkParameterMap.containsKey(parameterName)) {
                    throw new ClientParameterException(RmsException.COMPARE_RECIPE_PARAMETER_IS_NOT_EXIST, parameterName);
                }
            }

            for (String checkParameterName : checkParameterMap.keySet()) {
                RecipeEquipmentParameter checkRecipeParameter = checkParameterMap.get(checkParameterName);
                if (!parameterMap.containsKey(checkParameterName)) {
                    throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_EXPECT, checkParameterName);
                }
                RecipeEquipmentParameter recipeParameter = parameterMap.get(checkParameterName);
                recipeParameter.compare(checkRecipeParameter);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 变更checkSum
     * @param recipeEquipment
     * @param checkSum
     * @return
     * @throws ClientException
     */
    @Override
    public RecipeEquipment changeRecipeCheckSum(RecipeEquipment recipeEquipment, String checkSum) throws ClientException {
        try {
            recipeEquipment.setCheckSum(checkSum);
            recipeEquipment = recipeEquipmentRepository.saveAndFlush(recipeEquipment);
            baseService.saveHistoryEntity(recipeEquipment, RecipeEquipmentHis.TRANS_TYPE_CHANGE_CHECK_SUM);
            return recipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取得download的RECIPE并记录历史
     * @param lotId 进行加工的批次号
     * @param equipmentId 因为取得的可能是GOLDEN RECIPE 则历史会少记录设备号。故此处传输
     * @param recipeEquipmentRrn
     * @return
     * @throws ClientException
     */
    @Override
    public RecipeEquipment downloadRecipe(String lotId, String equipmentId, long recipeEquipmentRrn) throws ClientException {
        try {
//            RecipeEquipment recipeEquipment = getDeepRecipeEquipment(recipeEquipmentRrn);
//            // 记录download历史
//            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment);
//            recipeEquipmentHis.setEquipmentId(equipmentId);
//            recipeEquipmentHis.setTransType(RecipeEquipmentHis.TRANS_TYPE_DOWNLOAD);
//            recipeEquipmentHis.setEquipmentId(equipmentId);
//            recipeEquipmentHis.setLotId(lotId);
//            recipeEquipmentHisRepository.save(recipeEquipmentHis);
//
//            return recipeEquipment;
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据主键把ReicpeEquipment下的所有子recipe都查出来
     * @param recipeEquipmentRrn
     * @return
     * @throws ClientException
     */
    public RecipeEquipment getDeepRecipeEquipment(long recipeEquipmentRrn) throws ClientException {
        try {
            RecipeEquipment RecipeEquipment = recipeEquipmentRepository.getDeepRecipeEquipment(recipeEquipmentRrn);
            RecipeEquipment = getRecipeEquipment(RecipeEquipment, true);
            return RecipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取得recipeEquipment下所有的unitRecipe(递归)
     * @param recipeEquipment 设备Recipe
     * @param bodyFlag 是否要取得params
     * @return
     */
    public RecipeEquipment getRecipeEquipment(RecipeEquipment recipeEquipment, boolean bodyFlag) {
        try {
//            List<RecipeEquipmentUnit> units = recipeEquipmentUnitRepository.getByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
//            if (CollectionUtils.isNotEmpty(units)) {
//                List<RecipeEquipment> subRecipeEquipments = Lists.newArrayList();
//                for (RecipeEquipmentUnit unit : units) {
//                    RecipeEquipment subRecipeEquipment;
//                    if (bodyFlag) {
//                        subRecipeEquipment = recipeEquipmentRepository.getDeepRecipeEquipment(unit.getObjectRrn());
//                    } else {
//                        subRecipeEquipment = recipeEquipmentRepository.findByObjectRrn(unit.getUnitRecipeRrn());
//                    }
//                    if (subRecipeEquipment == null) {
//                        continue;
//                    }
//                    subRecipeEquipment = getRecipeEquipment(subRecipeEquipment, bodyFlag);
//                    subRecipeEquipments.add(subRecipeEquipment);
//                }
//                recipeEquipment.setSubRecipeEquipments(subRecipeEquipments);
//            }
            return recipeEquipment;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
    @Override
    public void recipeOnlineChange(RecipeEquipment recipeEquipment, Map<String, List<String>> contextParameterGroup, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException {
        try {
            for (String key : contextParameterGroup.keySet()) {
                recipeOnlineChange(recipeEquipment, contextParameterGroup.get(key), changeParameters, expriedPolicy, life);
            }
        } catch (ClientException e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取得临时修改的parameter值
     * @param recipeEquipment
     * @param contextParameters
     * @return
     * @throws ClientException
     */
    @Override
    public List<RecipeEquipmentParameterTemp> getOnlineRecipe(RecipeEquipment recipeEquipment, List<String> contextParameters) throws ClientException {
        try {
            Context context = contextService.getContextByName(RecipeEquipment.CONTEXT_RECIPE_EQUIPMENT);

            // 根据不同的parameter给contextFieldValue赋值
            ContextValue contextValue = new ContextValue();
            contextValue.setFieldValue1(String.valueOf(recipeEquipment.getObjectRrn()));
            if (contextParameters != null && contextParameters.size() > 0) {
                for (int i = 0; i < contextParameters.size(); i++) {
                    Field field = ContextValue.class.getDeclaredField("contextFieldValue" + (i + 2));
                    field.setAccessible(true);
                    field.set(contextValue, contextParameters.get(i));
                }
            }

            List<ContextValue> contextValues = contextService.getContextValue(context, contextValue);
            if (CollectionUtils.isNotEmpty(contextValues)) {
                Map<String, List<ContextValue>> tempMap = contextValues.stream().collect(Collectors.groupingBy(ContextValue :: getResultValue1));
                if (tempMap.size() > 1) {
                    throw new ClientException(RmsException.EQP_RECIPE_ONLINE_MULTI_CHANGE);
                } else {
                    ContextValue value = contextValues.get(0);
                    List<RecipeEquipmentParameterTemp> temps = recipeEquipmentParameterTempRepository.findByEcnIdAndStatus(value.getResultValue1(), DefaultStatusMachine.STATUS_ACTIVE);
                    return temps;
                }
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 工艺临时变更
     * @param recipeEquipment 变更的recipe
     * @param contextParameters 什么情况下变更 比如当A+B时变更 取值就只能A+B时才生效
     * @param changeParameters 变更的参数
     * @param expriedPolicy 失效策略 有次数和时间
     * @param life 周期 次数时表示多少次，时间时表示多少天
     * @throws ClientException
     */
    public void recipeOnlineChange(RecipeEquipment recipeEquipment, List<String> contextParameters,
                                   List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException{
        try {
            //1. 查找此Recipe是否已经设置了临时修改，如果有, 失效原本值
            List<RecipeEquipmentParameterTemp> temps = getOnlineRecipe(recipeEquipment, contextParameters);
            if (CollectionUtils.isNotEmpty(temps)) {
                for (RecipeEquipmentParameterTemp temp : temps) {
                    // 失效
                    temp.setStatus(DefaultStatusMachine.STATUS_INACTIVE);
                    recipeEquipmentParameterTempRepository.save(temp);
                }
            }
            Context context = contextService.getContextByName(RecipeEquipment.CONTEXT_RECIPE_EQUIPMENT);

            List<ContextValue> contextValues = Lists.newArrayList();
            // 通过ECNID关联相应的变更通知
            String ecnId = UUID.randomUUID().toString();
            for (RecipeEquipmentParameter parameter : changeParameters) {
                ContextValue contextValue = new ContextValue();
                contextValue.setContextRrn(context.getObjectRrn());
                contextValue.setFieldValue1(String.valueOf(recipeEquipment.getObjectRrn()));

                for (int i = 0; i < contextParameters.size(); i++) {
                    Field field = ContextValue.class.getDeclaredField("contextFieldValue" + (i + 2));
                    field.setAccessible(true);
                    field.set(contextValue, contextParameters.get(i));
                }

                // 如果已有原来的就失效
                List<ContextValue> activedContextValues = contextService.getContextValue(context, contextValue);
                if (activedContextValues != null && activedContextValues.size() > 0) {
                    for (ContextValue activedContextValue : activedContextValues) {
                        activedContextValue.setStatus(DefaultStatusMachine.STATUS_INACTIVE);
                        contextValueRepository.save(activedContextValue);
                    }
                }

                contextValue.setResultValue1(ecnId);
                contextValues.add(contextValue);

                RecipeEquipmentParameterTemp paraTemp = new RecipeEquipmentParameterTemp();
                paraTemp.setEcnId(ecnId);
                paraTemp.setStatus(DefaultStatusMachine.STATUS_ACTIVE);
                paraTemp.setRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
                paraTemp.setParameterName(parameter.getParameterName());
                paraTemp.setParameterGroup(parameter.getParameterGroup());
                paraTemp.setParameterValue(parameter.getCurrentValue());
                paraTemp.setMinValue(parameter.getMinValue());
                paraTemp.setMaxValue(parameter.getMaxValue());
                paraTemp.setCurrentCount(0);
                paraTemp.setLife(life == 0 ? 1 : life);
                paraTemp.setExpiredPolicy(StringUtils.isNullOrEmpty(expriedPolicy) ? RecipeEquipmentParameterTemp.EXPIRED_POLICY_COUNT : expriedPolicy);
                recipeEquipmentParameterTempRepository.save(paraTemp);
            }
            contextService.saveContextValue(context, contextValues);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
