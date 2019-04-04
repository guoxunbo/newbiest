package com.newbiest.mms.service.impl;

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
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
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
     * 根据packageMaterialLot获取被包装的批次
     * @param packagedMaterialLot
     * @param sc
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getSourceMLots(MaterialLot packagedMaterialLot, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            return materialLotRepository.findByParentMaterialLotRrnAndPackedFlag(packagedMaterialLot.getObjectRrn(), true);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 对物料批次进行包装
     * @return
     */
    public MaterialLot packageMLots(List<MaterialLot> materialLots, MaterialLotAction materialLotAction, String packageType, SessionContext sc) {
        try {
            sc.buildTransInfo();
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

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE_CREATE, sc);
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());

            // 记录被包装的批次信息
            String materialLotIds = StringUtils.join(materialLots.stream().map(MaterialLot :: getMaterialLotId).collect(Collectors.toList()), StringUtils.SEMICOLON_CODE);
            String actionComment = materialLotAction.getActionComment();
            // 超过最大长度追加的备注限制，则不追加记录信息
            if (materialLotIds.length() <= NBHis.MAX_APPEND_COMMENT_LENGTH) {
                actionComment += " PackedMLots [" + materialLotIds + "]";
            }
            history.setActionComment(actionComment);
            materialLotHistoryRepository.save(history);

            // 对物料批次做package事件处理
            for (MaterialLot materialLot : materialLots) {
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_PACKAGE, StringUtils.EMPTY, sc);
                materialLot.setPackedFlag(true);
                materialLot.setParentMaterialLotRrn(packedMaterialLot.getObjectRrn());
                materialLot.setParentMaterialLotId(packedMaterialLot.getMaterialLotId());
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                // 记录历史
                history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE, sc);
                history.setActionCode(materialLotAction.getActionCode());
                history.setActionReason(materialLotAction.getActionReason());
                history.setActionComment(materialLotAction.getActionComment());
                materialLotHistoryRepository.save(history);
            }
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
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
