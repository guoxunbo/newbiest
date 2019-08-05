package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotPackageTypeRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.PackagedLotDetailRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019/4/1.
 */
@Service
@Slf4j
@Transactional
public class PackageServiceImpl implements PackageService{

    @Autowired
    MaterialLotPackageTypeRepository materialLotPackageTypeRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    public MaterialLotPackageType getMaterialPackageTypeByName(String name, SessionContext sc) {
        List<MaterialLotPackageType> packageTypes = (List<MaterialLotPackageType>) materialLotPackageTypeRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
        if (CollectionUtils.isNotEmpty(packageTypes)) {
            return packageTypes.get(0);
        } else {
            throw new ClientParameterException(MmsException.MM_PACKAGE_TYPE_IS_NOT_EXIST, name);
        }
    }

    /**
     * 获取包装详细信息
     * @return
     */
    public List<MaterialLot> getPackageDetailsLot(Long packagedLotRrn) {
        try {
            return materialLotRepository.getPackageDetailsLot(packagedLotRrn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *
     * @param materialLotId
     * @param sc
     * @return
     */
    public MaterialLot getPackageLot(String materialLotId, SessionContext sc) {
        return materialLotRepository.findByMaterialLotIdAndCategoryAndOrgRrn(materialLotId, MaterialLot.CATEGORY_PACKAGE, sc.getOrgRrn());
    }

    /**
     * 追加包装
     *   当前不卡控被追加包装批次的状态
     * @param packedMaterialLot 被追加的包装批次
     * @param materialLotActions 包装批次动作
     * @param sc
     * @return
     */
    public MaterialLot additionalPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            // 取第一个的materialAction作为所有者的actionCode
            MaterialLotAction firstMaterialAction = materialLotActions.get(0);

            List<MaterialLot> allMaterialLot = Lists.newArrayList();

            packedMaterialLot = getPackageLot(packedMaterialLot.getMaterialLotId(), sc);
            if (packedMaterialLot == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST);
            }
            // 将包装的和以前包装的放在一起进行包装规则验证
            List<MaterialLot> packageDetailsLot = getPackageDetailsLot(packedMaterialLot.getObjectRrn());
            allMaterialLot.addAll(packageDetailsLot);

            List<MaterialLot> waitToAddPackingMLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), sc)).collect(Collectors.toList());
            allMaterialLot.addAll(waitToAddPackingMLots);

            MaterialLotPackageType materialLotPackageType = getMaterialPackageTypeByName(packedMaterialLot.getPackageType(), sc);
            materialLotPackageType.validationPacking(allMaterialLot);

            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(allMaterialLot));
            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);
            // 记录创建历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_ADDITIONAL_PACKAGE, sc);
            history.setActionCode(firstMaterialAction.getActionCode());
            history.setActionReason(firstMaterialAction.getActionReason());
            // 记录被包装的批次信息
            String materialLotIds = StringUtils.join(waitToAddPackingMLots.stream().map(MaterialLot :: getMaterialLotId).collect(Collectors.toList()), StringUtils.SEMICOLON_CODE);
            String actionComment = firstMaterialAction.getActionComment();
            // 超过最大长度追加的备注限制，则不追加记录信息
            if (materialLotIds.length() <= NBHis.MAX_APPEND_COMMENT_LENGTH) {
                actionComment += " PackedMLots [" + materialLotIds + "]";
            }
            history.setActionComment(actionComment);
            materialLotHistoryRepository.save(history);

            packageMaterialLots(packedMaterialLot, waitToAddPackingMLots, materialLotActions, sc);

            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 拆包装，
     *  当前只支持全部拆包装， 并不返还被包装的物料数量(需要通过盘点或者反消耗进行)
     * @param materialLotAction
     * @param sc
     */
    public void unPack(MaterialLotAction materialLotAction, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            MaterialLot packedMaterialLot = getPackageLot(materialLotAction.getMaterialLotId(), sc);
            if (packedMaterialLot == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST);
            }
            packedMaterialLot = mmsService.changeMaterialLotState(packedMaterialLot, MaterialEvent.EVENT_UN_PACKAGE, StringUtils.EMPTY, sc);
            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_CREATE_PACKAGE, sc);
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 对物料批次进行包装
     * @return
     */
    public MaterialLot packageMLots(List<MaterialLotAction> materialLotActions, String packageType, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            // 取第一个的materialAction作为所有者的actionCode
            MaterialLotAction firstMaterialAction = materialLotActions.get(0);

            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), sc)).collect(Collectors.toList());
            MaterialLotPackageType materialLotPackageType = getMaterialPackageTypeByName(packageType, sc);
            materialLotPackageType.validationPacking(materialLots);

            MaterialLot packedMaterialLot = (MaterialLot) materialLots.get(0).clone();
            String packedMaterialLotId = generatorPackageMLotId(packedMaterialLot, materialLotPackageType, sc);
            if (mmsService.getMLotByMLotId(packedMaterialLotId, sc) != null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }
            packedMaterialLot.setMaterialLotId(packedMaterialLotId);
            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(materialLots));
            packedMaterialLot.setMaterialType(materialLotPackageType.getTargetMaterialType());
            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);
            // 记录创建历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_CREATE_PACKAGE, sc);
            history.setActionCode(firstMaterialAction.getActionCode());
            history.setActionReason(firstMaterialAction.getActionReason());

            // 记录被包装的批次信息
            String materialLotIds = StringUtils.join(materialLots.stream().map(MaterialLot :: getMaterialLotId).collect(Collectors.toList()), StringUtils.SEMICOLON_CODE);
            String actionComment = firstMaterialAction.getActionComment();
            // 超过最大长度追加的备注限制，则不追加记录信息
            if (materialLotIds.length() <= NBHis.MAX_APPEND_COMMENT_LENGTH) {
                actionComment += " PackedMLots [" + materialLotIds + "]";
            }
            history.setActionComment(actionComment);
            materialLotHistoryRepository.save(history);

            packageMaterialLots(packedMaterialLot, materialLots, materialLotActions, sc);
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批次包装
     *  扣减源物料批次数量，记录包装详细信息
     * @param packedMaterialLot
     * @param waitToPackingLot
     * @param materialLotActions
     * @param sc
     */
    private void packageMaterialLots(MaterialLot packedMaterialLot, List<MaterialLot> waitToPackingLot, List<MaterialLotAction> materialLotActions, SessionContext sc) throws ClientException {
        // 对物料批次做package事件处理 扣减物料批次数量
        for (MaterialLot materialLot : waitToPackingLot) {

            String materialLotId = materialLot.getMaterialLotId();
            MaterialLotAction materialLotAction = materialLotActions.stream().filter(action -> materialLotId.equals(action.getMaterialLotId())).findFirst().get();
            BigDecimal currentQty = materialLot.getCurrentQty().subtract(materialLotAction.getTransQty());
            // 完全包完
            if (currentQty.compareTo(BigDecimal.ZERO) == 0) {
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_PACKAGE, MaterialStatus.STATUS_PACKED, sc);
            } else if (currentQty.compareTo(BigDecimal.ZERO) > 0) {
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_PACKAGE, StringUtils.EMPTY, sc);
            } else {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO, materialLot.getMaterialLotId());
            }

            materialLot.setCurrentQty(currentQty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            // 记录历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE, sc);
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);

            // 记录包装详情
            PackagedLotDetail packagedLotDetail = packagedLotDetailRepository.findByPackagedLotRrnAndMaterialLotRrn(packedMaterialLot.getObjectRrn(), materialLot.getObjectRrn());
            if (packagedLotDetail == null) {
                packagedLotDetail = new PackagedLotDetail();
                packagedLotDetail.setOrgRrn(sc.getOrgRrn());
                packagedLotDetail.setPackagedLotRrn(packedMaterialLot.getObjectRrn());
                packagedLotDetail.setPackagedLotId(packedMaterialLot.getMaterialLotId());
                packagedLotDetail.setMaterialLotRrn(materialLot.getObjectRrn());
                packagedLotDetail.setMaterialLotId(materialLot.getMaterialLotId());
            }
            packagedLotDetail.setQty(packagedLotDetail.getQty().add(materialLotAction.getTransQty()));
            packagedLotDetailRepository.save(packagedLotDetail);
        }
    }

    /**
     * 生成打包之后的物料批次号
     * @return
     * @throws ClientException
     */
    public String generatorPackageMLotId(MaterialLot packageMaterialLot, MaterialLotPackageType packageType, SessionContext sc) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setObject(packageMaterialLot);
            generatorContext.setRuleName(packageType.getPackIdRule());

            String id = generatorService.generatorId(sc.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
