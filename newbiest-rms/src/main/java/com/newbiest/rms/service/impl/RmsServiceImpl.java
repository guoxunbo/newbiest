package com.newbiest.rms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PreConditionalUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.context.model.Context;
import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.ContextValueRepository;
import com.newbiest.context.service.ContextService;
import com.newbiest.rms.exception.RmsException;
import com.newbiest.rms.model.*;
import com.newbiest.rms.repository.*;
import com.newbiest.rms.service.RmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Service
@Transactional
@Slf4j
public class RmsServiceImpl implements RmsService {

    @Autowired
    private AbstractRecipeEquipmentRepository abstractRecipeEquipmentRepository;

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

    /**
     * 保存recipeEquipment 如果存在以前的版本，则做升版本，不然就直接保存第一版本
     * @param recipeEquipment
     * @param sc
     * @return
     * @throws ClientException
     */
    public AbstractRecipeEquipment saveRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            String transType;
            if (recipeEquipment.getObjectRrn() == null) {
                transType = NBHis.TRANS_TYPE_CREATE;

                recipeEquipment.setOrgRrn(sc.getOrgRrn());
                recipeEquipment.setActiveFlag(true);
                recipeEquipment.setCreatedBy(sc.getUsername());
                recipeEquipment.setCreated(new Date());
                recipeEquipment.setUpdatedBy(sc.getUsername());
                recipeEquipment.setStatus(AbstractRecipeEquipment.STATUS_UNFROZEN);
                recipeEquipment.setPattern(StringUtils.isNullOrEmpty(recipeEquipment.getPattern()) ? AbstractRecipeEquipment.PATTERN_NORMAL : recipeEquipment.getPattern());
                // 取得激活的，没有存在激活的就取最高版本的，paramter的compareFlag/SpecialFlag以及range值都从原有版本上来
                List<AbstractRecipeEquipment> allRecipeEquipments = abstractRecipeEquipmentRepository.getRecipeEquipment(recipeEquipment.getOrgRrn(), recipeEquipment.getRecipeName(),
                                                                                                    recipeEquipment.getEquipmentId(), recipeEquipment.getEquipmentType(), recipeEquipment.getPattern());
                Map<String, RecipeEquipmentParameter> parameterMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(allRecipeEquipments)) {
                    AbstractRecipeEquipment lastRecipeEquipment = allRecipeEquipments.get(0);
                    recipeEquipment.setVersion(lastRecipeEquipment.getVersion() + 1);

                    Optional<AbstractRecipeEquipment> optional = allRecipeEquipments.stream().filter(temp -> NBVersionControl.STATUS_ACTIVE.equals(temp.getStatus())).findFirst();
                    if (optional.isPresent()) {
                        AbstractRecipeEquipment activeRecipeEquipment = optional.get();
                        activeRecipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(activeRecipeEquipment.getObjectRrn());
                        parameterMap = activeRecipeEquipment.getRecipeEquipmentParameters().stream().
                                collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));
                    } else {

                        lastRecipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(lastRecipeEquipment.getObjectRrn());
                        parameterMap = lastRecipeEquipment.getRecipeEquipmentParameters().stream().
                                collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));
                    }
                } else {
                    recipeEquipment.setVersion(1L);
                }

                /**
                 * 如果没有设备号，但是有设备类型，则保存为GoldenRecipe
                 */
                if (StringUtils.isNullOrEmpty(recipeEquipment.getEquipmentId()) && !StringUtils.isNullOrEmpty(recipeEquipment.getEquipmentType())) {
                    recipeEquipment.setGoldenFlag(true);
                }
                // 保存parameter 如果是升级从原有版本上拿到是否比较,是否特殊以及range范围
                for (RecipeEquipmentParameter parameter : recipeEquipment.getRecipeEquipmentParameters()) {
                    if (parameterMap.containsKey(parameter.getParameterName())) {
                        RecipeEquipmentParameter checkParameter = parameterMap.get(parameter.getParameterName());
                        parameter.setCompareFlag(checkParameter.getCompareFlag());
                        parameter.setSpecialParameterFlag(checkParameter.getSpecialParameterFlag());
                        if (RecipeEquipmentParameter.VALIDATE_TYPE_RANGE.equals(parameter.getValidateType()) && RecipeEquipmentParameter.VALIDATE_TYPE_RANGE.equals(checkParameter.getValidateType())) {
                            if (StringUtils.isNullOrEmpty(parameter.getMinValue()) && !StringUtils.isNullOrEmpty(checkParameter.getMinValue())) {
                                double minValue = Double.parseDouble(parameter.getMinValue());
                                double checkMinValue = Double.parseDouble(checkParameter.getMinValue());
                                parameter.setMinValue(String.valueOf(minValue < checkMinValue ? minValue : checkMinValue));
                            }
                            if (StringUtils.isNullOrEmpty(parameter.getMaxValue()) && !StringUtils.isNullOrEmpty(checkParameter.getMaxValue())) {
                                double maxValue = Double.parseDouble(parameter.getMaxValue());
                                double checkMaxValue = Double.parseDouble(checkParameter.getMaxValue());
                                parameter.setMinValue(String.valueOf(maxValue > checkMaxValue ? maxValue : checkMaxValue));
                            }
                        }
                    }
                    recipeEquipmentParameterRepository.save(parameter);
                }
            } else {
                transType = NBHis.TRANS_TYPE_UPDATE;
                recipeEquipmentParameterRepository.deleteByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
                for (RecipeEquipmentParameter parameter : recipeEquipment.getRecipeEquipmentParameters()) {
                    parameter.setObjectRrn(null);
                    parameter.setRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
                    recipeEquipmentParameterRepository.save(parameter);
                }
                recipeEquipment.setUpdatedBy(sc.getUsername());
            }
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setTransType(transType);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);

            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void deleteRecipeEquipment(Long recipeEquipmentRrn, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(recipeEquipmentRrn);
            if (!(AbstractRecipeEquipment.STATUS_UNFROZEN.equals(recipeEquipment.getStatus())
                    || AbstractRecipeEquipment.STATUS_INACTIVE.equals(recipeEquipment.getStatus()))) {
                throw new ClientException(RmsException.EQP_RECIPE_DELETE_ONLY_UNFROZEN_OR_INACTIVE);
            }

            //TODO 当前不记录unitHis
            // 删除所属的子Recipe关系
            recipeEquipmentUnitRepository.deleteByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
            // 删除自己是子recipe本身
            recipeEquipmentUnitRepository.deleteByUnitRecipeRrn(recipeEquipment.getObjectRrn());

            abstractRecipeEquipmentRepository.delete(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setUpdatedBy(sc.getUsername());
            recipeEquipmentHis.setTransType(NBHis.TRANS_TYPE_DELETE);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public AbstractRecipeEquipment frozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (recipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_GOLDEN_RECIPE);
            }

            recipeEquipment.setUpdated(sc.getTransTime());
            recipeEquipment.setUpdatedBy(sc.getUsername());
            recipeEquipment.setStatus(AbstractRecipeEquipment.STATUS_FROZEN);
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setTransType(AbstractRecipeEquipment.STATUS_FROZEN);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);

            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public AbstractRecipeEquipment unFrozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            recipeEquipment.setUpdated(sc.getTransTime());
            recipeEquipment.setUpdatedBy(sc.getUsername());
            recipeEquipment.setStatus(AbstractRecipeEquipment.STATUS_UNFROZEN);
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setTransType(AbstractRecipeEquipment.STATUS_UNFROZEN);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);

            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public AbstractRecipeEquipment activeRecipeEquipment(AbstractRecipeEquipment recipeEquipment, boolean isActiveGloden, boolean sendNotification, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            // 如果激活的是GoldenRecipe
            if (recipeEquipment.getGoldenFlag()) {
                // 检查是否存在相同的名称并且已经激活的GoldenRecipe。如果存在。则不能激活
                AbstractRecipeEquipment activeGoldenRecipeEquipment = abstractRecipeEquipmentRepository.getGoldenRecipe(sc.getOrgRrn(), recipeEquipment.getEquipmentType(), recipeEquipment.getRecipeName(), AbstractRecipeEquipment.STATUS_ACTIVE, recipeEquipment.getPattern(), false);
                if (activeGoldenRecipeEquipment != null) {
                    // 如果存在暂时不让激活
                    if (isActiveGloden) {
                        inActiveRecipeEquipment(activeGoldenRecipeEquipment, false, sc);
                    } else {
                        throw new ClientException(RmsException.EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST);
                    }
                }
            } else {
                // 不是GDRecipe 则先失效原有设备上的Recipe
                AbstractRecipeEquipment activeRecipeEquipment = abstractRecipeEquipmentRepository.getActiveRecipeEquipment(sc.getOrgRrn(), recipeEquipment.getRecipeName(), recipeEquipment.getEquipmentId(), recipeEquipment.getPattern(), false);
                if (activeRecipeEquipment != null) {
                    inActiveRecipeEquipment(activeRecipeEquipment, false, sc);
                }
            }
            recipeEquipment.setUpdatedBy(sc.getUsername());
            recipeEquipment.setStatus(AbstractRecipeEquipment.STATUS_ACTIVE);
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setTransType(AbstractRecipeEquipment.STATUS_ACTIVE);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);

            if (sendNotification) {
                Map<String, Object> notificationMap = Maps.newHashMap();
                // TODO 处理激活通知
//                notificationMap.put(SendRmsTransContext.KEY_ACTIVE_TYPE, recipeEquipment.getActiveType());
//                sendNotification(recipeEquipment.getRecipeName(), recipeEquipment.getEquipmentId(), recipeEquipment.getEquipmentType(), recipeEquipment.getPattern(), NotificationRequest.NOTIFICATION_TYPE_ACTIVE, notificationMap, sc);
            }
            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public AbstractRecipeEquipment inActiveRecipeEquipment(AbstractRecipeEquipment recipeEquipment, boolean checkGolenFlag, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (checkGolenFlag && recipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_GOLDEN_RECIPE);
            }

            recipeEquipment.setUpdated(sc.getTransTime());
            recipeEquipment.setUpdatedBy(sc.getUsername());
            recipeEquipment.setStatus(AbstractRecipeEquipment.STATUS_INACTIVE);
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setTransType(AbstractRecipeEquipment.STATUS_INACTIVE);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);
            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * ReleaseRecipe
     * @param abstractRecipeEquipment
     * @param sc
     * @throws ClientException
     */
    public void holdRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(abstractRecipeEquipment.getObjectRrn(), "RecipeEquipment ObjectRrn");
            if (AbstractRecipeEquipment.HOLD_STATE_ON.equals(abstractRecipeEquipment.getHoldState())) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_ALREADY_HOLD);
            }
            abstractRecipeEquipment.setHoldState(AbstractRecipeEquipment.HOLD_STATE_ON);
            abstractRecipeEquipmentRepository.saveAndFlush(abstractRecipeEquipment);

            RecipeEquipmentHis his = new RecipeEquipmentHis(abstractRecipeEquipment, sc);
            his.setActionCode(actionCode);
            his.setActionComment(actionComment);
            his.setActionReason(actionReason);
            his.setTransType(RecipeEquipmentHis.TRANS_TYPE_HOLD);
            recipeEquipmentHisRepository.save(his);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * ReleaseRecipe
     * @param abstractRecipeEquipment
     * @param sc
     * @throws ClientException
     */
    public void releaseRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(abstractRecipeEquipment.getObjectRrn(), "RecipeEquipment ObjectRrn");
            if (AbstractRecipeEquipment.HOLD_STATE_OFF.equals(abstractRecipeEquipment.getHoldState())) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_ALREADY_RELEASE);
            }
            abstractRecipeEquipment.setHoldState(AbstractRecipeEquipment.HOLD_STATE_OFF);
            abstractRecipeEquipmentRepository.saveAndFlush(abstractRecipeEquipment);

            RecipeEquipmentHis his = new RecipeEquipmentHis(abstractRecipeEquipment, sc);
            his.setActionCode(actionCode);
            his.setActionComment(actionComment);
            his.setActionReason(actionReason);
            his.setTransType(RecipeEquipmentHis.TRANS_TYPE_RELEASE);
            recipeEquipmentHisRepository.save(his);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void setGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (abstractRecipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST);
            }
            Equipment equipment = equipmentRepository.getByEquipmentId(abstractRecipeEquipment.getEquipmentId());
            if (equipment == null) {
                throw new ClientException(RmsException.EQP_IS_NOT_EXIST);
            }
            if (AbstractRecipeEquipment.STATUS_ACTIVE.equals(abstractRecipeEquipment.getStatus())) {
                AbstractRecipeEquipment recipeEqp = abstractRecipeEquipmentRepository.getGoldenRecipe(sc.getOrgRrn(), equipment.getEquipmentType(), abstractRecipeEquipment.getRecipeName(), AbstractRecipeEquipment.STATUS_ACTIVE, abstractRecipeEquipment.getPattern(), false);

                if (recipeEqp != null) {
                    throw new ClientException(RmsException.EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST);
                }
                // 设置成GoldenRecipe则需要清空EquipmentId
                abstractRecipeEquipment.setEquipmentId(null);
                abstractRecipeEquipment.setEquipmentType(equipment.getEquipmentType());
                abstractRecipeEquipment.setGoldenFlag(true);
                abstractRecipeEquipment.setUpdated(sc.getTransTime());
                abstractRecipeEquipment.setUpdatedBy(sc.getUsername());
                abstractRecipeEquipment = abstractRecipeEquipmentRepository.save(abstractRecipeEquipment);
                // 记录历史
                RecipeEquipmentHis recipeEqpHis = new RecipeEquipmentHis(abstractRecipeEquipment, sc);
                recipeEqpHis.setUpdated(sc.getTransTime());
                recipeEqpHis.setUpdatedBy(sc.getUsername());
                recipeEqpHis.setTransType(RecipeEquipmentHis.TRANS_TYPE_SET_GOLDEN);
                recipeEquipmentHisRepository.save(recipeEqpHis);
            } else {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_ACTIVE);
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }

    }

    @Override
    public void unSetGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment, String equipmentId, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (!abstractRecipeEquipment.getGoldenFlag()) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_GOLDEN);
            }
            if (!AbstractRecipeEquipment.STATUS_ACTIVE.equals(abstractRecipeEquipment.getStatus())) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_ACTIVE);
            }
            // TODO unset的时候是否卡控必输equipmentId
            if (!StringUtils.isNullOrEmpty(equipmentId)) {
                Equipment equipment = equipmentRepository.getByEquipmentId(equipmentId);
                if (equipment == null) {
                    throw new ClientException(RmsException.EQP_IS_NOT_EXIST);
                }
                abstractRecipeEquipment.setEquipmentId(equipment.getEquipmentId());
            }

            abstractRecipeEquipment.setGoldenFlag(false);
            abstractRecipeEquipment.setUpdated(sc.getTransTime());
            abstractRecipeEquipment.setUpdatedBy(sc.getUsername());
            abstractRecipeEquipment = abstractRecipeEquipmentRepository.save(abstractRecipeEquipment);
            // 记录历史
            RecipeEquipmentHis recipeEqpHis = new RecipeEquipmentHis(abstractRecipeEquipment, sc);
            recipeEqpHis.setUpdated(sc.getTransTime());
            recipeEqpHis.setUpdatedBy(sc.getUsername());
            recipeEqpHis.setTransType(RecipeEquipmentHis.TRANS_TYPE_UNSET_GOLDEN);
            recipeEquipmentHisRepository.save(recipeEqpHis);
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public void checkRecipeEquipmentBody(List<AbstractRecipeEquipment> recipeEquipmentList, List<String> tempValues, boolean checkHoldState, SessionContext sc)  throws ClientException{
        try {
            for (AbstractRecipeEquipment checkRecipeEquipment : recipeEquipmentList) {
                checkRecipeEquipmentBody(checkRecipeEquipment, tempValues, checkHoldState, sc);
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查Recipe一旦有一个不通过就立马抛出异常
     * @param checkRecipeEquipment 待检查的recipeEquipment
     * @param tempValues 临时值 比如当前的lot 当前的wafer等会去查找相应的RecipeParameterTemp
     * @param checkHoldState 是否检查Hold
     * @param sc
     * @throws ClientException
     */
    public void checkRecipeEquipmentBody(AbstractRecipeEquipment checkRecipeEquipment, List<String> tempValues, boolean checkHoldState, SessionContext sc)  throws ClientException{
        try {
            AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getActiveRecipeEquipment(sc.getOrgRrn(), checkRecipeEquipment.getRecipeName(), checkRecipeEquipment.getEquipmentId(), checkRecipeEquipment.getPattern(), true);
            if (recipeEquipment == null) {
                throw new ClientException(RmsException.EQP_RECIPE_IS_NOT_EXIST);
            }

            if (recipeEquipment.getBodyFlag()) {
                if (!StringUtils.isNullOrEmpty(recipeEquipment.getBody())) {
                    if (!recipeEquipment.getBody().equals(checkRecipeEquipment.getBody())) {
                        throw new ClientException(RmsException.EQP_RECIPE_BODY_NOT_SAME);
                    }
                } else {
                    throw new ClientException(RmsException.EQP_RECIPE_BODY_IS_NOT_EXIST);
                }
            }

            if (recipeEquipment.getTimestampFlag()) {
                if (recipeEquipment.getTimestamp() != null) {
                    long time = recipeEquipment.getTimestamp().getTime();
                    long checkTime = checkRecipeEquipment.getTimestamp().getTime();
                    if (time != checkTime) {
                        throw new ClientException(RmsException.EQP_RECIPE_TIMESTAMP_NOT_SAME);
                    }
                } else {
                    throw new ClientException(RmsException.EQP_RECIPE_TIMESTAMP_IS_NOT_EXIST);
                }
            }

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
                if (AbstractRecipeEquipment.HOLD_STATE_ON.equals(recipeEquipment.getHoldState())) {
                    throw new ClientException(RmsException.EQP_RECIPE_STATE_ON_HOLD);
                }
            }

            if (recipeEquipment.getParameterFlag()) {
                if (CollectionUtils.isNotEmpty(tempValues)) {
                    // 根据临时参数找到相应的临时修改参数
                    List<RecipeEquipmentParameterTemp> tempParameters = getOnlineRecipe(recipeEquipment, tempValues, sc);
                    if (CollectionUtils.isNotEmpty(tempParameters)) {
                        List<RecipeEquipmentParameter> checkRecipeParameters = checkRecipeEquipment.getRecipeEquipmentParameters();
                        for (RecipeEquipmentParameter parameter : checkRecipeParameters) {
                            Optional<RecipeEquipmentParameterTemp> optional = tempParameters.stream().filter(temp -> temp.getParameterName().equals(parameter.getParameterName()) && temp.getParameterGroup().equals(parameter.getParameterGroup())).findFirst();
                            if (optional.isPresent()) {
                                RecipeEquipmentParameterTemp temp = optional.get();
                                parameter.setDefaultValue(temp.getParameterValue());
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
            Map<String, RecipeEquipmentParameter> paramterMap = recipeEquipment.getRecipeEquipmentParameters()
                    .stream().filter(recipeEquipmentParameter -> recipeEquipmentParameter.getCompareFlag()).collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));

            Map<String, RecipeEquipmentParameter> checkParamterMap = checkRecipeEquipment.getRecipeEquipmentParameters()
                    .stream().collect(Collectors.toConcurrentMap(parameter -> parameter.getFullName(), Function.identity()));

            // 校验必须要compare的parameter是否在check列表中
            for (String parameterName : paramterMap.keySet()) {
                if (!checkParamterMap.containsKey(parameterName)) {
                    throw new ClientParameterException(RmsException.COMPARE_RECIPE_PARAMETER_IS_NOT_EXIST, parameterName);
                }
            }

            for (String checkParameterName : checkParamterMap.keySet()) {
                RecipeEquipmentParameter checkRecipeParameter = checkParamterMap.get(checkParameterName);
                if (!paramterMap.containsKey(checkParameterName)) {
                    throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_EXPECT, checkParameterName);
                }
                RecipeEquipmentParameter recipeParameter = paramterMap.get(checkParameterName);
                recipeParameter.compare(checkRecipeParameter);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 变更checkSum
     * @param recipeEquipment
     * @param checkSum
     * @param sc
     * @return
     * @throws ClientException
     */
    @Override
    public AbstractRecipeEquipment changeRecipeCheckSum(AbstractRecipeEquipment recipeEquipment, String checkSum, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            recipeEquipment.setUpdated(new Date());
            recipeEquipment.setUpdatedBy(sc.getUsername());
            recipeEquipment.setCheckSum(checkSum);
            recipeEquipment = abstractRecipeEquipmentRepository.saveAndFlush(recipeEquipment);

            RecipeEquipmentHis his = new RecipeEquipmentHis(recipeEquipment, sc);
            his.setTransType(RecipeEquipmentHis.TRANS_TYPE_CHANGE_CHECK_SUM);
            recipeEquipmentHisRepository.save(his);

            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 取得download的RECIPE并记录历史
     * @param lotId 进行加工的批次号
     * @param equipmentId 因为取得的可能是GOLDENRECIPE 则历史会少记录设备号。故此处传输
     * @param recipeEquipmentRrn
     * @return
     * @throws ClientException
     */
    @Override
    public AbstractRecipeEquipment downloadRecipe(String lotId, String equipmentId, long recipeEquipmentRrn, SessionContext sc) throws ClientException {
        try {
            AbstractRecipeEquipment recipeEquipment = getDeepRecipeEquipment(recipeEquipmentRrn, sc);
            // 记录download历史
            RecipeEquipmentHis recipeEquipmentHis = new RecipeEquipmentHis(recipeEquipment, sc);
            recipeEquipmentHis.setEquipmentId(equipmentId);
            recipeEquipmentHis.setUpdated(new Date());
            recipeEquipmentHis.setTransType(RecipeEquipmentHis.TRANS_TYPE_DOWNLOAD);
            recipeEquipmentHis.setEquipmentId(equipmentId);
            recipeEquipmentHis.setLotId(lotId);
            recipeEquipmentHisRepository.save(recipeEquipmentHis);

            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据主键把ReicpeEquipment下的所有子recipe都查出来
     * @param recipeEquipmentRrn
     * @return
     * @throws ClientException
     */
    public AbstractRecipeEquipment getDeepRecipeEquipment(long recipeEquipmentRrn, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            AbstractRecipeEquipment abstractRecipeEquipment = abstractRecipeEquipmentRepository.getDeepRecipeEquipment(recipeEquipmentRrn);
            abstractRecipeEquipment = getRecipeEquipment(abstractRecipeEquipment, true, sc);
            return abstractRecipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 取得recipeEquipment下所有的unitRecipe(递归)
     * @param recipeEquipment 设备Recipe
     * @param bodyFlag 是否要取得params
     * @param sc
     * @return
     */
    public AbstractRecipeEquipment getRecipeEquipment(AbstractRecipeEquipment recipeEquipment, boolean bodyFlag, SessionContext sc) {
        try {
            sc.buildTransInfo();
            List<RecipeEquipmentUnit> units = recipeEquipmentUnitRepository.getByRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
            if (CollectionUtils.isNotEmpty(units)) {
                List<AbstractRecipeEquipment> subRecipeEquipments = Lists.newArrayList();
                for (RecipeEquipmentUnit unit : units) {
                    AbstractRecipeEquipment subRecipeEquipment;
                    if (bodyFlag) {
                        subRecipeEquipment = abstractRecipeEquipmentRepository.getDeepRecipeEquipment(unit.getObjectRrn());
                    } else {
                        subRecipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(unit.getUnitRecipeRrn());
                    }
                    if (subRecipeEquipment == null) {
                        continue;
                    }
                    subRecipeEquipment = getRecipeEquipment(subRecipeEquipment, bodyFlag, sc);
                    subRecipeEquipments.add(subRecipeEquipment);
                }
                recipeEquipment.setSubRecipeEquipments(subRecipeEquipments);
            }
            return recipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }
    @Override
    public void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, Map<String, List<String>> contextParameterGroup, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life, SessionContext sc) throws ClientException {
        try {
            for (String key : contextParameterGroup.keySet()) {
                recipeOnlineChange(recipeEquipment, contextParameterGroup.get(key), changeParameters, expriedPolicy, life, sc);
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 取得临时修改的parameter值
     * @param recipeEquipment
     * @param contextParameters
     * @param sc
     * @return
     * @throws ClientException
     */
    @Override
    public List<RecipeEquipmentParameterTemp> getOnlineRecipe(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters, SessionContext sc) throws ClientException {
        try {
            Context context = contextService.getContextByName(AbstractRecipeEquipment.CONTEXT_RECIPE_EQUIPMENT);

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

            List<ContextValue> contextValues = contextService.getContextValue(context, contextValue, sc);
            if (CollectionUtils.isNotEmpty(contextValues)) {
                Map<String, List<ContextValue>> tempMap = contextValues.stream().collect(Collectors.groupingBy(ContextValue :: getResultValue1));
                if (tempMap.size() > 1) {
                    throw new ClientException(RmsException.EQP_RECIPE_ONLINE_MULTI_CHANGE);
                } else {
                    ContextValue value = contextValues.get(0);
                    List<RecipeEquipmentParameterTemp> temps = recipeEquipmentParameterTempRepository.getByEcnId(value.getResultValue1(), NBVersionControl.STATUS_ACTIVE, sc);
                    return temps;
                }
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 工艺临时变更
     * @param recipeEquipment 变更的recipe
     * @param contextParameters 什么情况下变更 比如当A+B时变更 取值就只能A+B时才生效
     * @param changeParameters 变更的参数
     * @param expriedPolicy 失效策略 有次数和时间
     * @param life 周期 次数时表示多少次，时间时表示多少天
     * @param sc
     * @throws ClientException
     */
    public void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters,
                                   List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life, SessionContext sc) throws ClientException{
        try {
            //1. 查找此Recipe是否已经设置了临时修改，如果有, 失效原本值
            List<RecipeEquipmentParameterTemp> temps = getOnlineRecipe(recipeEquipment, contextParameters, sc);
            if (CollectionUtils.isNotEmpty(temps)) {
                for (RecipeEquipmentParameterTemp temp : temps) {
                    // 失效
                    temp.setStatus(NBVersionControl.STATUS_INACTIVE);
                    recipeEquipmentParameterTempRepository.save(temp);
                }
            }
            Context context = contextService.getContextByName(AbstractRecipeEquipment.CONTEXT_RECIPE_EQUIPMENT);

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
                List<ContextValue> activedContextValues = contextService.getContextValue(context, contextValue, sc);
                if (activedContextValues != null && activedContextValues.size() > 0) {
                    for (ContextValue activedContextValue : activedContextValues) {
                        activedContextValue.setStatus(NBVersionControl.STATUS_INACTIVE);
                        contextValueRepository.save(activedContextValue);
                    }
                }

                contextValue.setResultValue1(ecnId);
                contextValues.add(contextValue);

                RecipeEquipmentParameterTemp paraTemp = new RecipeEquipmentParameterTemp();
                paraTemp.setOrgRrn(sc.getOrgRrn());
                paraTemp.setCreated(sc.getTransTime());
                paraTemp.setCreatedBy(sc.getUsername());
                paraTemp.setUpdated(sc.getTransTime());
                paraTemp.setUpdatedBy(sc.getUsername());

                paraTemp.setEcnId(ecnId);
                paraTemp.setStatus(NBVersionControl.STATUS_ACTIVE);
                paraTemp.setRecipeEquipmentRrn(recipeEquipment.getObjectRrn());
                paraTemp.setParameterName(parameter.getParameterName());
                paraTemp.setParameterGroup(parameter.getParameterGroup());
                paraTemp.setParameterValue(parameter.getDefaultValue());
                paraTemp.setMinValue(parameter.getMinValue());
                paraTemp.setMaxValue(parameter.getMaxValue());
                paraTemp.setCurrentCount(0);
                paraTemp.setLife(life == 0 ? 1 : life);
                paraTemp.setExpiredPolicy(StringUtils.isNullOrEmpty(expriedPolicy) ? RecipeEquipmentParameterTemp.EXPIRED_POLICY_COUNT : expriedPolicy);
                recipeEquipmentParameterTempRepository.save(paraTemp);
            }
            contextService.saveContextValue(context, contextValues, sc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
