package com.newbiest.gc.thread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.scm.dto.TempFtModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.msg.ResponseHeader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 导入物料批次的线程
 * 返回一个Callable结果
 * @author guozhangLuo
 * @date 2022-06-01 14:21
 */
@Data
@Slf4j
public class FTImportMLotUnitThread implements Callable {

    private String lotId;
    private String importCode;
    private String productCategory;
    private String importType;
    private String targetWaferSource;
    private String packageType;
    private String fileName;
    private Material material;
    private Date createHisDate;
    private Warehouse warehouse;
    private Storage storage;
    private List<TempFtModel> tempFtModelList;
    private TempFtModel firstTempFtModel;

    private MmsService mmsService;
    private BaseService baseService;
    private PackageService packageService;
    private MaterialLotRepository materialLotRepository;
    private MaterialLotHistoryRepository materialLotHistoryRepository;
    private MaterialLotUnitRepository materialLotUnitRepository;
    private MaterialLotUnitHisRepository materialLotUnitHisRepository;
    private SessionContext sessionContext;

    @Override
    public FTImportMLotThreadResult call()  {
        ThreadLocalContext.putSessionContext(sessionContext);
        FTImportMLotThreadResult result = new FTImportMLotThreadResult();
        try {
            List<String> materialLotIdList = Lists.newArrayList();
            //waferSource为7和9的晶圆一个unitId为一个lot，按照unit接收发料
            if(MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE.equals(targetWaferSource) || MaterialLot.SENSOR_WAFER_SOURCE.equals(targetWaferSource)){
                for(TempFtModel tempFtModel : tempFtModelList){
                    Map<String, Object> propMap = Maps.newConcurrentMap();
                    propMap.put("lotId", tempFtModel.getWaferId().trim());
                    String cstId = tempFtModel.getWaferId().trim().split("-")[0];
                    BigDecimal qty = new BigDecimal(tempFtModel.getWaferNum());
                    MaterialLotAction materialLotAction = buildMaterialLotAction(qty, BigDecimal.ONE, tempFtModel, propMap, cstId);
                    MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, tempFtModel.getWaferId().trim(), materialLotAction);
                    createMaterialLotUnitAndSaveHis(tempFtModel, materialLot);
                    materialLotIdList.add(materialLot.getMaterialLotId());
                }
            } else {
                Long totalMLotQty = tempFtModelList.stream().collect(Collectors.summingLong(tempCpModel -> Long.parseLong(tempCpModel.getWaferNum())));
                BigDecimal qty = new BigDecimal(totalMLotQty);
                BigDecimal subQty = new BigDecimal(tempFtModelList.size());
                Map<String, Object> propMap = Maps.newConcurrentMap();
                propMap.put("lotId", lotId.trim());
                MaterialLotAction materialLotAction = buildMaterialLotAction(qty, subQty, firstTempFtModel, propMap, StringUtils.EMPTY);
                MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, firstTempFtModel.getLotId(), materialLotAction);
                for (TempFtModel tempFtModel : tempFtModelList) {
                    createMaterialLotUnitAndSaveHis(tempFtModel, materialLot);
                }
                materialLotIdList.add(materialLot.getMaterialLotId());
            }
            result.setMaterialLotIdList(materialLotIdList);
        } catch (Exception e) {
            result.setResultMessage(e.getMessage() + lotId);
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }

    /**
     * 构建MaterialLotAction
     * @param currentQty
     * @param currentSubQty
     * @param waferTempFtModel
     * @return
     * @throws ClientException
     */
    private MaterialLotAction buildMaterialLotAction(BigDecimal currentQty, BigDecimal currentSubQty, TempFtModel waferTempFtModel, Map<String,Object> propMap, String cstId) throws ClientException{
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(currentQty);
            materialLotAction.setGrade(waferTempFtModel.getGrade());
            materialLotAction.setTransCount(currentSubQty);
            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTargetStorageId(storage.getName());
            materialLotAction.setTargetStorageRrn(storage.getObjectRrn());
            materialLotAction.setStorage(storage);
            materialLotAction.setWarehouse(warehouse);

            propMap.put("reserved8", waferTempFtModel.getBoxId() == null ? "": waferTempFtModel.getBoxId().trim());
            propMap.put("reserved13", materialLotAction.getTargetWarehouseRrn().toString());
            propMap.put("reserved14", waferTempFtModel.getPointId() == null ? "": waferTempFtModel.getPointId().trim());
            propMap.put("created", waferTempFtModel.getInTime());
            propMap.put("receiveDate", waferTempFtModel.getInTime());
            propMap.put("grade", waferTempFtModel.getGrade()  == null ? "": waferTempFtModel.getGrade().trim());
            propMap.put("reserved1", waferTempFtModel.getSecondCode() == null ? "": waferTempFtModel.getSecondCode().trim());
            propMap.put("reserved3", waferTempFtModel.getSaleRemarkDesc() == null ? "": waferTempFtModel.getSaleRemarkDesc().trim());
            propMap.put("reserved4", waferTempFtModel.getProdRemarkDesc() == null ? "": waferTempFtModel.getProdRemarkDesc().trim());
            propMap.put("reserved6", waferTempFtModel.getLocation() == null ? "" : waferTempFtModel.getLocation().trim());
            propMap.put("reserved22", waferTempFtModel.getVendor() == null ? "": waferTempFtModel.getVendor().trim());
            propMap.put("reserved24", waferTempFtModel.getFabDevice() == null ? "": waferTempFtModel.getFabDevice().trim());
            propMap.put("reserved27", waferTempFtModel.getPoNo() == null ? "": waferTempFtModel.getPoNo().trim());
            propMap.put("reserved29", waferTempFtModel.getInvoiceId() == null ? "": waferTempFtModel.getInvoiceId().trim());
            propMap.put("reserved32", waferTempFtModel.getWaferNum() == null ? "": waferTempFtModel.getWaferNum().trim());
            propMap.put("reserved33", waferTempFtModel.getDataValue19() == null ? "": waferTempFtModel.getDataValue19().trim());
            propMap.put("reserved34", waferTempFtModel.getPassNum() == null ? "": waferTempFtModel.getPassNum().trim());
            propMap.put("reserved35", waferTempFtModel.getNgNum() == null ? "": waferTempFtModel.getNgNum().trim());
            propMap.put("reserved36", waferTempFtModel.getYield() == null ? "": waferTempFtModel.getYield().trim());
            propMap.put("reserved37", waferTempFtModel.getPackLotId() == null ? "": waferTempFtModel.getPackLotId().trim());
            propMap.put("reserved38", waferTempFtModel.getDataValue16() == null ? "": waferTempFtModel.getDataValue16().trim());
            propMap.put("reserved39", waferTempFtModel.getCartonNo() == null ? "": waferTempFtModel.getCartonNo().trim());
            propMap.put("reserved41", waferTempFtModel.getRemark() == null ? "": waferTempFtModel.getRemark().trim());
            propMap.put("reserved42", waferTempFtModel.getDataValue20() == null ? "": waferTempFtModel.getDataValue20().trim());
            propMap.put("reserved43", waferTempFtModel.getDataValue24() == null ? "": waferTempFtModel.getDataValue24().trim());
            propMap.put("reserved45", waferTempFtModel.getDataValue25() == null ? "": waferTempFtModel.getDataValue25().trim());
            propMap.put("reserved46", waferTempFtModel.getWoId() == null ? "": waferTempFtModel.getWoId().trim());
            propMap.put("reserved7", productCategory);
            propMap.put("reserved47", fileName);
            propMap.put("reserved48", importCode);
            propMap.put("reserved49", importType);
            propMap.put("reserved50", targetWaferSource);

            if (waferTempFtModel.getDataValue8().equals("1")) {
                propMap.put("holdState", MaterialLot.HOLD_STATE_ON);
                propMap.put("holdReason", waferTempFtModel.getHoldDesc() == null ? "": waferTempFtModel.getHoldDesc().trim());
            }
            if(!StringUtils.isNullOrEmpty(waferTempFtModel.getDataValue12()) && waferTempFtModel.getDataValue12().equals("Y")){
                propMap.put("reserved9", waferTempFtModel.getDataValue12() == null ? "": waferTempFtModel.getDataValue12().trim());
            }
            if(!StringUtils.isNullOrEmpty(waferTempFtModel.getDataValue13()) && waferTempFtModel.getDataValue13().equals("Y")){
                propMap.put("reserved10", waferTempFtModel.getDataValue13() == null ? "": waferTempFtModel.getDataValue13().trim());
            }
            if(!StringUtils.isNullOrEmpty(waferTempFtModel.getDataValue14()) && MaterialLotUnit.PRODUCT_TYPE_ENG.equals(waferTempFtModel.getDataValue14())){
                propMap.put("productType", MaterialLotUnit.PRODUCT_TYPE_ENG);
            }
            if(!StringUtils.isNullOrEmpty(cstId)){
                propMap.put("durable", cstId);
                propMap.put("lotCst", cstId);
            } else {
                propMap.put("durable", waferTempFtModel.getCstId() == null ? "": waferTempFtModel.getCstId().trim());
                propMap.put("lotCst", waferTempFtModel.getCstId() == null ? "": waferTempFtModel.getCstId().trim());
            }
            propMap.put("packDevice", waferTempFtModel.getPackDevice() == null ? "": waferTempFtModel.getPackDevice().trim());
            propMap.put("materialCode", waferTempFtModel.getMaterialId() == null ? "": waferTempFtModel.getMaterialId().trim());
            propMap.put("vboxQrcodeInfo", waferTempFtModel.getVqrId() == null ? "": waferTempFtModel.getVqrId().trim());
            propMap.put("boxQrcodeInfo", waferTempFtModel.getBqrId() == null ? "": waferTempFtModel.getBqrId().trim());
            propMap.put("sourceProductId", waferTempFtModel.getDataValue29() == null ? "": waferTempFtModel.getDataValue29().trim());
            propMap.put("engineerName", waferTempFtModel.getDataValue3() == null ? "": waferTempFtModel.getDataValue3().trim());
            propMap.put("testPurpose", waferTempFtModel.getDataValue4() == null ? "": waferTempFtModel.getDataValue4().trim());
            propMap.put("workRemarks", waferTempFtModel.getDataValue5() == null ? "": waferTempFtModel.getDataValue5().trim());
            materialLotAction.setPropsMap(propMap);
            return materialLotAction;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建晶圆信息并且记录历史
     * @param tempFtModel
     * @param materialLot
     * @throws ClientException
     */
    private void createMaterialLotUnitAndSaveHis(TempFtModel tempFtModel, MaterialLot materialLot) throws ClientException{
        try {
            MaterialLotUnit materialLotUnit = new MaterialLotUnit();
            materialLotUnit.setUnitId(tempFtModel.getWaferId().trim());
            materialLotUnit.setMaterial(material);
            materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
            materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotUnit.setLotId(materialLot.getLotId());
            materialLotUnit.setState(MaterialLotUnit.STATE_IN);
            materialLotUnit.setGrade(materialLot.getGrade());
            materialLotUnit.setCreated(tempFtModel.getInTime());
            materialLotUnit.setReceiveDate(tempFtModel.getInTime());
            materialLotUnit.setCurrentQty(new BigDecimal(tempFtModel.getWaferNum().trim()));
            materialLotUnit.setReceiveQty(new BigDecimal(tempFtModel.getWaferNum().trim()));
            materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
            materialLotUnit.setPackDevice(materialLot.getPackDevice());
            materialLotUnit.setEngineerName(materialLot.getEngineerName());
            materialLotUnit.setWorkRemarks(materialLot.getWorkRemarks());
            materialLotUnit.setTestPurpose(materialLot.getTestPurpose());
            materialLotUnit.setProductType(materialLot.getProductType());
            materialLotUnit.setDurable(materialLot.getDurable());
            materialLotUnit.setLotCst(materialLot.getLotCst());
            materialLotUnit.setTreasuryNote(tempFtModel.getProdRemarkDesc() == null ? "": tempFtModel.getProdRemarkDesc().trim());
            materialLotUnit.setReserved1(tempFtModel.getSecondCode() == null ? "": tempFtModel.getSecondCode().trim());
            materialLotUnit.setReserved4(tempFtModel.getLocation() == null ? "": tempFtModel.getLocation().trim());
            materialLotUnit.setReserved8(materialLot.getReserved8());
            materialLotUnit.setReserved13(materialLot.getReserved13());
            materialLotUnit.setReserved14(tempFtModel.getPointId() == null ? "": tempFtModel.getPointId().trim());
            materialLotUnit.setReserved22(tempFtModel.getVendor() == null ? "": tempFtModel.getVendor().trim());
            materialLotUnit.setReserved24(tempFtModel.getFabDevice() == null ? "": tempFtModel.getFabDevice().trim());
            materialLotUnit.setReserved27(tempFtModel.getPoNo() == null ? "": tempFtModel.getPoNo().trim());
            materialLotUnit.setReserved29(tempFtModel.getInvoiceId() == null ? "": tempFtModel.getInvoiceId().trim());
            materialLotUnit.setReserved32(tempFtModel.getWaferNum() == null ? "": tempFtModel.getWaferNum().trim());
            materialLotUnit.setReserved33(tempFtModel.getDataValue19() == null ? "": tempFtModel.getDataValue19().trim());
            materialLotUnit.setReserved34(tempFtModel.getPassNum() == null ? "": tempFtModel.getPassNum().trim());
            materialLotUnit.setReserved35(tempFtModel.getNgNum() == null ? "": tempFtModel.getNgNum().trim());
            materialLotUnit.setReserved36(tempFtModel.getYield() == null ? "": tempFtModel.getYield().trim());
            materialLotUnit.setReserved37(tempFtModel.getPackLotId() == null ? "": tempFtModel.getPackLotId().trim());
            materialLotUnit.setReserved38(tempFtModel.getDataValue16() == null ? "": tempFtModel.getDataValue16().trim());
            materialLotUnit.setReserved39(tempFtModel.getCartonNo() == null ? "": tempFtModel.getCartonNo().trim());
            materialLotUnit.setReserved41(tempFtModel.getRemark() == null ? "": tempFtModel.getRemark().trim());
            materialLotUnit.setReserved42(tempFtModel.getDataValue20() == null ? "": tempFtModel.getDataValue20().trim());
            materialLotUnit.setReserved43(tempFtModel.getDataValue24() == null ? "": tempFtModel.getDataValue24().trim());
            materialLotUnit.setReserved45(tempFtModel.getDataValue25() == null ? "": tempFtModel.getDataValue25().trim());
            materialLotUnit.setReserved46(tempFtModel.getWoId() == null ? "": tempFtModel.getWoId().trim());
            materialLotUnit.setReserved47(fileName);
            materialLotUnit.setReserved49(importType);
            materialLotUnit.setReserved48(importCode);
            materialLotUnit.setReserved50(targetWaferSource);
            materialLotUnitRepository.save(materialLotUnit);

            //晶圆创建历史
            MaterialLotUnitHistory unitCreateHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
            unitCreateHistory.setCreated(materialLotUnit.getCreated());
            unitCreateHistory.setState(MaterialLotUnit.STATE_CREATE);
            materialLotUnitHisRepository.save(unitCreateHistory);

            //晶圆接收历史
            MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
            unitHistory.setCreated(createHisDate);
            unitHistory.setState(MaterialLotUnit.STATE_IN);
            materialLotUnitHisRepository.save(unitHistory);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
