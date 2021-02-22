package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.scm.dto.TempCpModel;
import com.newbiest.gc.service.TempService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.gc.service.GcService;

import com.newbiest.mms.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 格科临时使用service。
 *  用来处理老系统数据转到新系统
 * @author guoxunbo
 * @date 2/19/21 10:41 AM
 */
@Deprecated
@Service
@Transactional
@Slf4j
public class TempServiceImpl implements TempService {

    @Autowired
    MmsService mmsService;

    @Autowired
    GcService gcService;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    PackageService packageService;

    /**
     * 转换老系统的CP数据
     * @throws ClientException
     */
    public void transferCpData(List<TempCpModel> tempCpModelList) throws ClientException {
        try {
            String testLotIdPrefix = "GuoT";

            Map<String, Material> materialMap = Maps.newHashMap();

            Set<String> materialNameSet = tempCpModelList.stream().map(TempCpModel :: getDataValue18).collect(Collectors.toSet());
            for (String materialName : materialNameSet) {
                // 如果是-2则是rawMaterial.其他都是产品
                if (materialName.endsWith("-2")) {
                    RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                    if (rawMaterial == null) {
                        rawMaterial = new RawMaterial();
                        rawMaterial.setName(materialName);
                        rawMaterial = (RawMaterial) mmsService.createRawMaterial(rawMaterial);
                    }
                    materialMap.put(materialName, rawMaterial);
                } else {
                    Product product = mmsService.getProductByName(materialName);
                    if (product == null) {
                        gcService.saveProductAndSetStatusModelRrn(materialName);
                    }
                    materialMap.put(materialName, product);
                }
            }

            Map<String, List<TempCpModel>> lotUnitMap = tempCpModelList.stream().collect(Collectors.groupingBy(TempCpModel :: getLotId));
            for (String lotId : lotUnitMap.keySet()) {

                List<TempCpModel> lotTempCpModels = lotUnitMap.get(lotId);
                TempCpModel firstTempCpModel = lotTempCpModels.get(0);
                Material material = materialMap.get(firstTempCpModel.getDataValue18());

                MaterialLot materialLot = new MaterialLot();

                BigDecimal qty = new BigDecimal(firstTempCpModel.getDataValue7());
                BigDecimal subQty = new BigDecimal(firstTempCpModel.getCstWaferQty());

                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(qty);
                materialLotAction.setGrade(firstTempCpModel.getWaferType());
                materialLotAction.setTransCount(subQty);

                if (!StringUtils.isNullOrEmpty(firstTempCpModel.getStockId())) {
                    Warehouse warehouse = mmsService.getWarehouseByName(firstTempCpModel.getStockId());
                    if (warehouse == null) {
                        throw new ClientException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST);
                    }
                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                    materialLotAction.setTargetStorageId(firstTempCpModel.getPointId());
                }

                Map<String, Object> propMap = Maps.newConcurrentMap();
                propMap.put("reserved13", firstTempCpModel.getStockId());
                propMap.put("reserved14", firstTempCpModel.getPointId());

                propMap.put("created", firstTempCpModel.getInTime());
                propMap.put("reserved1", firstTempCpModel.getSecondCode());
                propMap.put("reserved6", firstTempCpModel.getLocation());
                propMap.put("reserved22", firstTempCpModel.getVendor());
                propMap.put("reserved27", firstTempCpModel.getPoNo());
                propMap.put("reserved46", firstTempCpModel.getWoId());
                propMap.put("reserved24", firstTempCpModel.getFabDevice());
                propMap.put("reserved39", firstTempCpModel.getCartonNo());
                propMap.put("reserved29", firstTempCpModel.getInvoiceId());
                propMap.put("reserved25", firstTempCpModel.getDataValue5());
                propMap.put("reserved4", firstTempCpModel.getProdRemarkDesc());

                String holdReason = firstTempCpModel.getDataValue6();
                if (!StringUtils.isNullOrEmpty(holdReason)) {
                    propMap.put("holdState", MaterialLot.HOLD_STATE_ON);
                    propMap.put("holdReason", holdReason);
                }
                propMap.put("lotId", testLotIdPrefix + lotId);
                materialLotAction.setPropsMap(propMap);

                materialLot = mmsService.receiveMLot2Warehouse(material, testLotIdPrefix + lotId, materialLotAction);

                for (TempCpModel tempCpModel : lotTempCpModels) {
                    MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                    materialLotUnit.setUnitId(testLotIdPrefix + tempCpModel.getWaferId());
                    materialLotUnit.setMaterial(material);
                    materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                    materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setReserved13(tempCpModel.getStockId());
                    materialLotUnit.setReserved14(tempCpModel.getPointId());
                    materialLotUnit.setCreated(tempCpModel.getInTime());
                    materialLotUnit.setReserved1(tempCpModel.getSecondCode());

                    materialLotUnit.setReserved6(tempCpModel.getLocation());
                    materialLotUnit.setReserved22(tempCpModel.getVendor());
                    materialLotUnit.setReserved27(tempCpModel.getPoNo());
                    materialLotUnit.setReserved46(tempCpModel.getWoId());

                    materialLotUnit.setReserved24(tempCpModel.getFabDevice());
                    materialLotUnit.setReserved39(tempCpModel.getCartonNo());
                    materialLotUnit.setReserved29(tempCpModel.getInvoiceId());
                    materialLotUnit.setReserved25(tempCpModel.getDataValue5());
                    materialLotUnitRepository.save(materialLotUnit);
                    //TODO 记录接收历史
                }

            }

            // 处理包装
            Map<String, List<TempCpModel>> boxedTempCpModelMap = tempCpModelList.stream().filter(tempCpModel -> !StringUtils.isNullOrEmpty(tempCpModel.getBoxId())).collect(Collectors.groupingBy(TempCpModel::getBoxId));

            for (String boxId : boxedTempCpModelMap.keySet()) {
                List<TempCpModel> boxDetails = boxedTempCpModelMap.get(boxId);

                Set<String> boxedLotIds = boxDetails.stream().map(TempCpModel::getBoxId).collect(Collectors.toSet());
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (String boxedLotId : boxedLotIds) {
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(testLotIdPrefix + boxedLotId);
                    materialLotActions.add(materialLotAction);
                }
                packageService.packageMLots(materialLotActions, boxId, "WltPackCase");

            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
