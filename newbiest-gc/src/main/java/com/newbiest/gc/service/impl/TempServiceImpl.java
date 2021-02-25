package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
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

    @Autowired
    BaseService baseService;

    /**
     * 转换老系统的CP数据
     * @throws ClientException
     */
    public void transferCpData(List<TempCpModel> tempCpModelList, String fileName) throws ClientException {
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
                    Material product = mmsService.getProductByName(materialName);
                    if (product == null) {
                        product = gcService.saveProductAndSetStatusModelRrn(materialName);
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
                Long totalMLotQty = lotTempCpModels.stream().collect(Collectors.summingLong(tempCpModel -> Long.parseLong(tempCpModel.getDataValue7())));

                BigDecimal qty = new BigDecimal(totalMLotQty);
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
                getImportTypeAndWaferSourceByMaterialName(propMap, material.getName());
                if(MaterialLot.WAREHOUSE_SH.equals(firstTempCpModel.getStockId())){
                    propMap.put("reserved13", MaterialLot.SH_WAREHOUSE);
                } else if(MaterialLot.WAREHOUSE_ZJ.equals(firstTempCpModel.getStockId())){
                    propMap.put("reserved13", MaterialLot.ZJ_WAREHOUSE);
                } else {
                    propMap.put("reserved13", MaterialLot.HK_WAREHOUSE);
                }
                propMap.put("reserved14", firstTempCpModel.getPointId() == null ? "": firstTempCpModel.getPointId().trim());

                propMap.put("created", firstTempCpModel.getInTime());
                propMap.put("receiveDate", firstTempCpModel.getInTime());
                propMap.put("reserved1", firstTempCpModel.getSecondCode() == null ? "": firstTempCpModel.getSecondCode().trim());
                propMap.put("reserved6", firstTempCpModel.getLocation() == null ? "" : firstTempCpModel.getLocation().trim());
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_CP);//CP0
                propMap.put("reserved22", firstTempCpModel.getVendor() == null ? "": firstTempCpModel.getVendor().trim());
                propMap.put("reserved27", firstTempCpModel.getPoNo() == null ? "": firstTempCpModel.getPoNo().trim());
                propMap.put("reserved46", firstTempCpModel.getWoId() == null ? "": firstTempCpModel.getWoId().trim());
                propMap.put("reserved24", firstTempCpModel.getFabDevice() == null ? "": firstTempCpModel.getFabDevice().trim());
                propMap.put("reserved39", firstTempCpModel.getCartonNo() == null ? "": firstTempCpModel.getCartonNo().trim());
                propMap.put("reserved29", firstTempCpModel.getInvoiceId() == null ? "": firstTempCpModel.getInvoiceId().trim());
                propMap.put("reserved25", firstTempCpModel.getDataValue5() == null ? "": firstTempCpModel.getDataValue5().trim());
                propMap.put("reserved4", firstTempCpModel.getProdRemarkDesc() == null ? "": firstTempCpModel.getProdRemarkDesc().trim());
                propMap.put("reserved47", fileName);

                String holdReason = firstTempCpModel.getDataValue6() == null ? "": firstTempCpModel.getDataValue6().trim();
                if (firstTempCpModel.getDataValue8().equals("1")) {
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
                    materialLotUnit.setLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setCurrentQty(new BigDecimal(tempCpModel.getDataValue7()));
                    materialLotUnit.setReceiveQty(new BigDecimal(tempCpModel.getDataValue7()));
                    materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnit.setReserved14(tempCpModel.getPointId());
                    materialLotUnit.setCreated(tempCpModel.getInTime());
                    materialLotUnit.setReceiveDate(tempCpModel.getInTime());
                    materialLotUnit.setReserved1(tempCpModel.getSecondCode());

                    materialLotUnit.setReserved4(tempCpModel.getLocation());
                    materialLotUnit.setReserved22(tempCpModel.getVendor());
                    materialLotUnit.setReserved27(tempCpModel.getPoNo());
                    materialLotUnit.setReserved46(tempCpModel.getWoId());

                    materialLotUnit.setReserved24(tempCpModel.getFabDevice());
                    materialLotUnit.setReserved39(tempCpModel.getCartonNo());
                    materialLotUnit.setReserved29(tempCpModel.getInvoiceId());
                    materialLotUnit.setReserved25(tempCpModel.getDataValue5());
                    materialLotUnit.setReserved47(fileName);
                    materialLotUnit.setReserved49(materialLot.getReserved49());
                    materialLotUnit.setReserved50(materialLot.getReserved50());
                    materialLotUnitRepository.save(materialLotUnit);

                    //晶圆创建历史
                    MaterialLotUnitHistory unitCreateHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                    unitCreateHistory.setCreated(materialLotUnit.getCreated());
                    materialLotUnitHisRepository.save(unitCreateHistory);

                    //晶圆接收历史
                    MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                    unitHistory.setCreated(materialLotUnit.getCreated());
                    unitHistory.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnitHisRepository.save(unitHistory);
                }

            }

            // 处理包装
            Map<String, List<TempCpModel>> boxedTempCpModelMap = tempCpModelList.stream().filter(tempCpModel -> !StringUtils.isNullOrEmpty(tempCpModel.getBoxId().trim())).collect(Collectors.groupingBy(TempCpModel::getBoxId));

            for (String boxId : boxedTempCpModelMap.keySet()) {
                List<TempCpModel> boxDetails = boxedTempCpModelMap.get(boxId);

                Set<String> boxedLotIds = boxDetails.stream().map(TempCpModel::getLotId).collect(Collectors.toSet());
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (String boxedLotId : boxedLotIds) {
                    MaterialLot materialLot = mmsService.getMLotByMLotId(testLotIdPrefix + boxedLotId);
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotAction.setTransQty(materialLot.getCurrentQty());
                    materialLotActions.add(materialLotAction);
                }
                packageService.packageMLots(materialLotActions, boxId, "WltPackCase");

            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产品型号获取导入型号和waferSource
     * @param propMap
     * @param materialName
     * @throws ClientException
     */
    private void getImportTypeAndWaferSourceByMaterialName(Map<String,Object> propMap, String materialName) throws ClientException{
        try {
            if(materialName.endsWith("-2.5")){
                propMap.put("reserved49", MaterialLot.IMPORT_LCD_CP);//LCD_CP
                propMap.put("reserved50", MaterialLot.LCP_IN_FLAG_WAFER_SOURCE);//3
            } else if(materialName.endsWith("-2.6")) {
                propMap.put("reserved49", MaterialLot.IMPORT_LCD_CP);//LCD_CP
                propMap.put("reserved50", MaterialLot.LCP_WAFER_SOURCE);//4
            } else if(materialName.endsWith("-2") || materialName.endsWith("-3")){
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                propMap.put("reserved50", MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);//1
            } else if(materialName.endsWith("-2.1")){
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                propMap.put("reserved50", MaterialLot.SCP_WAFER_SOURCE);//2
            } else if(materialName.endsWith("-1")){
                if(materialName.startsWith("GC7") || materialName.startsWith("GC9")){
                    propMap.put("reserved49", MaterialLot.IMPORT_LCD_CP);//LCD_CP
                    propMap.put("reserved50", MaterialLot.LCP_IN_FLAG_WAFER_SOURCE);//3
                } else {
                    propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                    propMap.put("reserved50", MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);//1
                }
            } else if(materialName.endsWith("-1.1")){
                if(materialName.startsWith("GC7") || materialName.startsWith("GC9")){
                    propMap.put("reserved49", MaterialLot.IMPORT_LCD_CP);//LCD_CP
                    propMap.put("reserved50", MaterialLot.LCP_WAFER_SOURCE);//4
                } else {
                    propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                    propMap.put("reserved50", MaterialLot.SCP_WAFER_SOURCE);//2
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


}
