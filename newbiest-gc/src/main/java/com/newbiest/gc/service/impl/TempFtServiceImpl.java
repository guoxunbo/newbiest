package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.scm.dto.TempFtModel;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.TempFtService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 格科临时使用service。
 *  老系统FT数据转到新系统
 * @author luoguozhang
 * @date 2022/2/11
 */
@Deprecated
@Service
@Transactional
@Slf4j
public class TempFtServiceImpl implements TempFtService {

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
     * 转换老系统的FT数据
     * @throws ClientException
     */
    public void transferFtData(List<TempFtModel> tempCpModelList, String fileName) throws ClientException {
        try {
            Map<String, Material> materialMap = Maps.newHashMap();
            Map<String, List<TempFtModel>> waferSourceMap = tempCpModelList.stream().collect(Collectors.groupingBy(TempFtModel :: getWaferSource));
            for (String waferSource : waferSourceMap.keySet()) {
                List<TempFtModel> tempFtModels = waferSourceMap.get(waferSource);
                //区分真空包和wafer处理,lotId为空的则为真空包，不为空则为wafer
                List<TempFtModel> vboxList = tempFtModels.stream().filter(tempFtModel -> StringUtils.isNullOrEmpty(tempFtModel.getLotId().trim())).collect(Collectors.toList());
                List<TempFtModel> lotUnitList = tempFtModels.stream().filter(tempFtModel -> !StringUtils.isNullOrEmpty(tempFtModel.getLotId().trim())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(vboxList)){
                    for(TempFtModel tempFtModel : vboxList){
                        Material material = validateAndGetMaterial(waferSource, tempFtModel.getProductId().trim());
                        BigDecimal currentQty = new BigDecimal(tempFtModel.getWaferNum());

                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setTransQty(currentQty);
                        materialLotAction.setGrade(tempFtModel.getGrade());

                        if (!StringUtils.isNullOrEmpty(tempFtModel.getStockId())) {
                            Warehouse warehouse = getWareHoseByStockId(tempFtModel.getStockId().trim());
                            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                            materialLotAction.setTargetStorageId(tempFtModel.getPointId());
                        }

                        Map<String, Object> propMap = Maps.newConcurrentMap();
                        getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(propMap, waferSource);
                        buildPropMap(propMap, tempFtModel, materialLotAction, fileName);

                        materialLotAction.setPropsMap(propMap);

                        MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, tempFtModel.getWaferId(), materialLotAction);

                        //没有装箱并且做过出货检验的修改状态
                        if(!StringUtils.isNullOrEmpty(materialLot.getReserved10())){
                            if(!StringUtils.isNullOrEmpty(tempFtModel.getBoxId()) && (tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_B) || tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_SBB))){
                                checkOutAndSaveHis(materialLot);
                            }
                        }
                    }

                    //处理装箱的真空包
                    Map<String, List<TempFtModel>> boxedTempFtModelMap = tempCpModelList.stream().filter(tempFtModel -> !StringUtils.isNullOrEmpty(tempFtModel.getBoxId().trim()) &&
                            (tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_B) || tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_SBB))).collect(Collectors.groupingBy(TempFtModel::getBoxId));

                    for (String parentMaterialLotId : boxedTempFtModelMap.keySet()) {
                        List<TempFtModel> boxInfoList = boxedTempFtModelMap.get(parentMaterialLotId);
                        Set<String> vboxIdList = boxInfoList.stream().map(TempFtModel::getWaferId).collect(Collectors.toSet());
                        List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                        for (String boxedLotId : vboxIdList) {
                            MaterialLot materialLot = mmsService.getMLotByMLotId(boxedLotId);
                            MaterialLotAction materialLotAction = new MaterialLotAction();
                            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                            materialLotAction.setTransQty(materialLot.getCurrentQty());
                            materialLotActions.add(materialLotAction);
                        }
                        MaterialLot packedMaterialLot = packageService.packageMLots(materialLotActions, parentMaterialLotId, "PackCase");

                        //检验箱中是否存在已经做过出货检验的真空包
                        List<TempFtModel> checkOutList = boxInfoList.stream().filter(tempFtModel -> !StringUtils.isNullOrEmpty(tempFtModel.getDataValue13()) && tempFtModel.getDataValue13().equals("Y")).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(checkOutList)){
                            checkOutAndSaveHis(packedMaterialLot);
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(lotUnitList)){
                    Map<String, List<TempFtModel>> lotUnitMap = lotUnitList.stream().collect(Collectors.groupingBy(TempFtModel :: getLotId));
                    for(String lotId : lotUnitMap.keySet()){
                        List<TempFtModel> lotTempCpModels = lotUnitMap.get(lotId);
                        TempFtModel firstTempFtModel = lotTempCpModels.get(0);
                        Material material = validateAndGetMaterial(waferSource, firstTempFtModel.getProductId());

                        Long totalMLotQty = lotTempCpModels.stream().collect(Collectors.summingLong(tempCpModel -> Long.parseLong(tempCpModel.getWaferNum())));
                        BigDecimal qty = new BigDecimal(totalMLotQty);
                        BigDecimal subQty = new BigDecimal(lotTempCpModels.size());

                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setTransQty(qty);
                        materialLotAction.setGrade(firstTempFtModel.getGrade());
                        materialLotAction.setTransCount(subQty);

                        if (!StringUtils.isNullOrEmpty(firstTempFtModel.getStockId())) {
                            Warehouse warehouse = getWareHoseByStockId(firstTempFtModel.getStockId().trim());
                            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                            materialLotAction.setTargetStorageId(firstTempFtModel.getPointId());
                        }
                        Map<String, Object> propMap = Maps.newConcurrentMap();
                        getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(propMap, waferSource);
                        buildPropMap(propMap, firstTempFtModel, materialLotAction, fileName);
                        propMap.put("lotId", firstTempFtModel.getLotId() == null ? "": firstTempFtModel.getLotId().trim());
                        materialLotAction.setPropsMap(propMap);

                        MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, firstTempFtModel.getLotId(), materialLotAction);

                        for (TempFtModel tempFtModel : lotTempCpModels) {
                            MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                            materialLotUnit.setUnitId(tempFtModel.getWaferId());
                            materialLotUnit.setMaterial(material);
                            materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                            materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                            materialLotUnit.setLotId(materialLot.getLotId());
                            materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                            materialLotUnit.setGrade(materialLot.getGrade());
                            materialLotUnit.setCreated(tempFtModel.getInTime());
                            materialLotUnit.setReceiveDate(tempFtModel.getInTime());
                            materialLotUnit.setCurrentQty(new BigDecimal(tempFtModel.getWaferNum()));
                            materialLotUnit.setReceiveQty(new BigDecimal(tempFtModel.getWaferNum()));
                            materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                            materialLotUnit.setPackDevice(materialLot.getPackDevice());
                            materialLotUnit.setEngineerName(materialLot.getEngineerName());
                            materialLotUnit.setWorkRemarks(materialLot.getWorkRemarks());
                            materialLotUnit.setTestPurpose(materialLot.getTestPurpose());
                            materialLotUnit.setDurable(materialLot.getDurable());
                            materialLotUnit.setLotCst(materialLot.getLotCst());
                            materialLotUnit.setTreasuryNote(tempFtModel.getProdRemarkDesc());
                            materialLotUnit.setReserved1(tempFtModel.getSecondCode());
                            materialLotUnit.setReserved4(tempFtModel.getLocation());
                            materialLotUnit.setReserved8(materialLot.getReserved8());
                            materialLotUnit.setReserved13(materialLot.getReserved13());
                            materialLotUnit.setReserved14(tempFtModel.getPointId());
                            materialLotUnit.setReserved22(tempFtModel.getVendor());
                            materialLotUnit.setReserved24(tempFtModel.getFabDevice());
                            materialLotUnit.setReserved27(tempFtModel.getPoNo());
                            materialLotUnit.setReserved29(tempFtModel.getInvoiceId());
                            materialLotUnit.setReserved33(tempFtModel.getDataValue19());
                            materialLotUnit.setReserved34(tempFtModel.getPassNum());
                            materialLotUnit.setReserved35(tempFtModel.getNgNum());
                            materialLotUnit.setReserved36(tempFtModel.getYield());
                            materialLotUnit.setReserved37(tempFtModel.getPackLotId());
                            materialLotUnit.setReserved38(tempFtModel.getDataValue16());
                            materialLotUnit.setReserved39(tempFtModel.getCartonNo());
                            materialLotUnit.setReserved41(tempFtModel.getRemark());
                            materialLotUnit.setReserved42(tempFtModel.getDataValue20());
                            materialLotUnit.setReserved43(tempFtModel.getDataValue24());
                            materialLotUnit.setReserved45(tempFtModel.getDataValue25());
                            materialLotUnit.setReserved46(tempFtModel.getWoId());
                            materialLotUnit.setReserved47(fileName);
                            materialLotUnit.setReserved49(materialLot.getReserved49());
                            materialLotUnit.setReserved50(materialLot.getReserved50());
                            materialLotUnitRepository.save(materialLotUnit);

                            //晶圆创建历史
                            MaterialLotUnitHistory unitCreateHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                            unitCreateHistory.setCreated(materialLotUnit.getCreated());
                            unitCreateHistory.setState(MaterialLotUnit.STATE_CREATE);
                            materialLotUnitHisRepository.save(unitCreateHistory);

                            //晶圆接收历史
                            MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                            unitHistory.setCreated(getDate(materialLotUnit.getCreated()));
                            unitHistory.setState(MaterialLotUnit.STATE_IN);
                            materialLotUnitHisRepository.save(unitHistory);
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取三分钟后的时间
     * @param created
     * @return
     * @throws ClientException
     */
    private Date getDate(Date created) throws ClientException{
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(created);
            calendar.add(Calendar.MINUTE, 3);
            return calendar.getTime();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 构建物料批次参数
     * @param propMap
     * @param tempFtModel
     * @param materialLotAction
     * @param fileName
     * @throws ClientException
     */
    private void buildPropMap(Map<String,Object> propMap, TempFtModel tempFtModel, MaterialLotAction materialLotAction, String fileName) throws ClientException{
        try {
            if(!StringUtils.isNullOrEmpty(tempFtModel.getStockId())){
                propMap.put("reserved13", materialLotAction.getTargetWarehouseRrn().toString());
            }
            propMap.put("reserved14", tempFtModel.getPointId() == null ? "": tempFtModel.getPointId().trim());
            if(!StringUtils.isNullOrEmpty(tempFtModel.getBoxId()) && !tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_B) && !tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_SBB)){
                propMap.put("reserved8", tempFtModel.getBoxId() == null ? "": tempFtModel.getBoxId().trim());
            }
            propMap.put("created", tempFtModel.getInTime());
            propMap.put("receiveDate", tempFtModel.getInTime());
            propMap.put("reserved1", tempFtModel.getSecondCode() == null ? "": tempFtModel.getSecondCode().trim());
            propMap.put("reserved3", tempFtModel.getSaleRemarkDesc() == null ? "": tempFtModel.getSaleRemarkDesc().trim());
            propMap.put("reserved4", tempFtModel.getProdRemarkDesc() == null ? "": tempFtModel.getProdRemarkDesc().trim());
            propMap.put("reserved6", tempFtModel.getLocation() == null ? "" : tempFtModel.getLocation().trim());
            propMap.put("reserved22", tempFtModel.getVendor() == null ? "": tempFtModel.getVendor().trim());
            propMap.put("reserved24", tempFtModel.getFabDevice() == null ? "": tempFtModel.getFabDevice().trim());
            propMap.put("reserved27", tempFtModel.getPoNo() == null ? "": tempFtModel.getPoNo().trim());
            propMap.put("reserved29", tempFtModel.getInvoiceId() == null ? "": tempFtModel.getInvoiceId().trim());
            propMap.put("reserved33", tempFtModel.getDataValue19() == null ? "": tempFtModel.getDataValue19().trim());
            propMap.put("reserved34", tempFtModel.getPassNum() == null ? "": tempFtModel.getPassNum().trim());
            propMap.put("reserved35", tempFtModel.getNgNum() == null ? "": tempFtModel.getNgNum().trim());
            propMap.put("reserved36", tempFtModel.getYield() == null ? "": tempFtModel.getYield().trim());
            propMap.put("reserved37", tempFtModel.getPackLotId() == null ? "": tempFtModel.getPackLotId().trim());
            propMap.put("reserved38", tempFtModel.getDataValue16() == null ? "": tempFtModel.getDataValue16().trim());
            propMap.put("reserved39", tempFtModel.getCartonNo() == null ? "": tempFtModel.getCartonNo().trim());
            propMap.put("reserved41", tempFtModel.getRemark() == null ? "": tempFtModel.getRemark().trim());
            propMap.put("reserved42", tempFtModel.getDataValue20() == null ? "": tempFtModel.getDataValue20().trim());
            propMap.put("reserved43", tempFtModel.getDataValue24() == null ? "": tempFtModel.getDataValue24().trim());
            propMap.put("reserved45", tempFtModel.getDataValue25() == null ? "": tempFtModel.getDataValue25().trim());
            propMap.put("reserved46", tempFtModel.getWoId() == null ? "": tempFtModel.getWoId().trim());
            propMap.put("reserved47", fileName);

            if (tempFtModel.getDataValue8().equals("1")) {
                propMap.put("holdState", MaterialLot.HOLD_STATE_ON);
                propMap.put("holdReason", tempFtModel.getHoldDesc() == null ? "": tempFtModel.getHoldDesc().trim());
            }
            if(!StringUtils.isNullOrEmpty(tempFtModel.getDataValue12()) && tempFtModel.getDataValue12().equals("Y")){
                propMap.put("reserved9", tempFtModel.getDataValue12() == null ? "": tempFtModel.getDataValue12().trim());
            }
            if(!StringUtils.isNullOrEmpty(tempFtModel.getDataValue13()) && tempFtModel.getDataValue13().equals("Y")){

                propMap.put("reserved10", tempFtModel.getDataValue13() == null ? "": tempFtModel.getDataValue13().trim());
            }
            if(!StringUtils.isNullOrEmpty(tempFtModel.getDataValue14()) && MaterialLotUnit.PRODUCT_TYPE_ENG.equals(tempFtModel.getDataValue14())){
                propMap.put("productType", MaterialLotUnit.PRODUCT_TYPE_ENG);
            }

            if(!StringUtils.isNullOrEmpty(tempFtModel.getCstId())){
                propMap.put("durable", tempFtModel.getCstId() == null ? "": tempFtModel.getCstId().trim());
                propMap.put("lotCst", tempFtModel.getCstId() == null ? "": tempFtModel.getCstId().trim());
            }
            propMap.put("packDevice", tempFtModel.getPackDevice() == null ? "": tempFtModel.getPackDevice().trim());
            propMap.put("materialCode", tempFtModel.getMaterialId() == null ? "": tempFtModel.getMaterialId().trim());
            propMap.put("vboxQrcodeInfo", tempFtModel.getVqrId() == null ? "": tempFtModel.getVqrId().trim());
            propMap.put("boxQrcodeInfo", tempFtModel.getBqrId() == null ? "": tempFtModel.getBqrId().trim());
            propMap.put("sourceProductId", tempFtModel.getDataValue29() == null ? "": tempFtModel.getDataValue29().trim());
            propMap.put("engineerName", tempFtModel.getDataValue3() == null ? "": tempFtModel.getDataValue3().trim());
            propMap.put("testPurpose", tempFtModel.getDataValue4() == null ? "": tempFtModel.getDataValue4().trim());
            propMap.put("workRemarks", tempFtModel.getDataValue5() == null ? "": tempFtModel.getDataValue5().trim());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 出货检验并且记录历史
     * @param materialLot
     * @throws ClientException
     */
    private void checkOutAndSaveHis(MaterialLot materialLot) throws ClientException{
        try {
            materialLot = mmsService.changeMaterialLotState(materialLot, "OQC", "OK");

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "OQC");
            history.setCreated(getDate(materialLot.getCreated()));
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据仓库名称获取仓库信息
     * @param stockId
     * @return
     * @throws ClientException
     */
    private Warehouse getWareHoseByStockId(String stockId) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(stockId);
            if (warehouse == null) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, stockId);
            }
            return warehouse;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据waferSource获取产品号或者晶圆型号
     * @param waferSource
     * @param productId
     * @return
     * @throws ClientException
     */
    private Material validateAndGetMaterial(String waferSource, String productId) throws ClientException{
        try {
            Material material = new Material();
            if(TempFtModel.WAFER_SOURCE_LIST_4.contains(waferSource) || TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                if(TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                    productId += "-4.7";
                } else{
                    productId += "-4";
                }
                material = mmsService.getProductByName(productId);
                if (material == null) {
                    material = gcService.saveProductAndSetStatusModelRrn(productId);
                }
            } else if(TempFtModel.WAFER_SOURCE_LIST_35.contains(waferSource)){
                productId += "-3.5";
                material = mmsService.getRawMaterialByName(productId);
                if (material == null) {
                    RawMaterial rawMaterial = new RawMaterial();
                    rawMaterial.setName(productId);
                    material =  mmsService.createRawMaterial(rawMaterial);
                }
            }
            return  material;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据老系统WaferSouce获取导入型号和waferSource
     * @param propMap
     * @param waferSource
     * @throws ClientException
     */
    private void getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(Map<String,Object> propMap, String waferSource) throws ClientException{
        try {
            if(TempFtModel.WAFER_SOURCE_1.equals(waferSource) || TempFtModel.WAFER_SOURCE_3.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);//SENSOR0
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR);//SENSOR
                propMap.put("reserved50", MaterialLot.SENSOR_WAFER_SOURCE);//9
            } else if(TempFtModel.WAFER_SOURCE_2.equals(waferSource)) {
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                propMap.put("reserved49", MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                propMap.put("reserved50", MaterialLot.FT_WAFER_SOURCE);//10
            } else if(TempFtModel.WAFER_SOURCE_11.equals(waferSource) || TempFtModel.WAFER_SOURCE_12.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_COG);//COG0
                propMap.put("reserved49", MaterialLot.IMPORT_COG);//COG
                propMap.put("reserved50", MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);//17
            } else if(TempFtModel.WAFER_SOURCE_21.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_FT_COB);//COB
                propMap.put("reserved49", MaterialLot.IMPORT_COB);//COB
                propMap.put("reserved50", MaterialLot.RW_WAFER_SOURCE);//20
            } else if(TempFtModel.WAFER_SOURCE_31.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RW);//RW
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                propMap.put("reserved50", MaterialLot.RW_WAFER_SOURCE);//20
            } else if(TempFtModel.WAFER_SOURCE_32.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_RMA);//RMA
                propMap.put("reserved49", MaterialLot.IMPORT_CRMA);//CRMA
                propMap.put("reserved50", MaterialLot.RW_WAFER_SOURCE);//15
            } else if(TempFtModel.WAFER_SOURCE_33.equals(waferSource) || TempFtModel.WAFER_SOURCE_34.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_WLT);//WLT0
                propMap.put("reserved49", MaterialLot.IMPORT_WLT);//WLT
                propMap.put("reserved50", MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE);//7
            } else if(TempFtModel.WAFER_SOURCE_39.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_SOC);//SOC0
                propMap.put("reserved49", MaterialLot.IMPORT_SOC);//SOC
                propMap.put("reserved50", MaterialLot.SOC_WAFER_SOURCE);//18
            } else if(TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                propMap.put("reserved7", MaterialLot.PRODUCT_CATEGORY);//COM
                propMap.put("reserved49", MaterialLot.PRODUCT_CATEGORY);//COM
                propMap.put("reserved50", MaterialLot.COM_WAFER_SOURCE);//19
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}