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

@Data
@Slf4j
public class FTImportCobMLotUnitThread implements Callable {

    private String durable;
    private String parentMaterialLotId;
    private String importCode;
    private String productCategory;
    private String importType;
    private String targetWaferSource;
    private String shipper;
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
            Long totalMLotQty = tempFtModelList.stream().collect(Collectors.summingLong(tempCpModel -> Long.parseLong(tempCpModel.getWaferNum())));
            BigDecimal currentQty = new BigDecimal(totalMLotQty);
            BigDecimal currentSubQty = new BigDecimal(tempFtModelList.size());
            Map<String, Object> propMap = Maps.newConcurrentMap();
            propMap.put("lotId", durable.trim());
            propMap.put("durable", durable.trim());
            propMap.put("reserved13", warehouse.getObjectRrn().toString());
            propMap.put("reserved14", firstTempFtModel.getPointId() == null ? "" : firstTempFtModel.getPointId().trim());
            propMap.put("created", firstTempFtModel.getInTime());
            propMap.put("receiveDate", firstTempFtModel.getInTime());
            propMap.put("grade", firstTempFtModel.getGrade()  == null ? "": firstTempFtModel.getGrade().trim());
            propMap.put("reserved1", firstTempFtModel.getSecondCode() == null ? "": firstTempFtModel.getSecondCode().trim());
            propMap.put("reserved3", firstTempFtModel.getSaleRemarkDesc() == null ? "": firstTempFtModel.getSaleRemarkDesc().trim());
            propMap.put("reserved4", firstTempFtModel.getProdRemarkDesc() == null ? "": firstTempFtModel.getProdRemarkDesc().trim());
            propMap.put("reserved6", firstTempFtModel.getLocation() == null ? "" : firstTempFtModel.getLocation().trim());
            propMap.put("reserved22", firstTempFtModel.getVendor() == null ? "": firstTempFtModel.getVendor().trim());
            propMap.put("reserved24", firstTempFtModel.getFabDevice() == null ? "": firstTempFtModel.getFabDevice().trim());
            propMap.put("reserved27", firstTempFtModel.getPoNo() == null ? "": firstTempFtModel.getPoNo().trim());
            propMap.put("reserved29", firstTempFtModel.getInvoiceId() == null ? "": firstTempFtModel.getInvoiceId().trim());
            propMap.put("reserved32", firstTempFtModel.getWaferNum() == null ? "": firstTempFtModel.getWaferNum().trim());
            propMap.put("reserved33", firstTempFtModel.getDataValue19() == null ? "": firstTempFtModel.getDataValue19().trim());
            propMap.put("reserved34", firstTempFtModel.getPassNum() == null ? "": firstTempFtModel.getPassNum().trim());
            propMap.put("reserved35", firstTempFtModel.getNgNum() == null ? "": firstTempFtModel.getNgNum().trim());
            propMap.put("reserved36", firstTempFtModel.getYield() == null ? "": firstTempFtModel.getYield().trim());
            propMap.put("reserved37", firstTempFtModel.getPackLotId() == null ? "": firstTempFtModel.getPackLotId().trim());
            propMap.put("reserved38", firstTempFtModel.getDataValue16() == null ? "": firstTempFtModel.getDataValue16().trim());
            propMap.put("reserved39", firstTempFtModel.getCartonNo() == null ? "": firstTempFtModel.getCartonNo().trim());
            propMap.put("reserved41", firstTempFtModel.getRemark() == null ? "": firstTempFtModel.getRemark().trim());
            propMap.put("reserved42", firstTempFtModel.getDataValue20() == null ? "": firstTempFtModel.getDataValue20().trim());
            propMap.put("reserved43", firstTempFtModel.getDataValue24() == null ? "": firstTempFtModel.getDataValue24().trim());
            propMap.put("reserved45", firstTempFtModel.getDataValue25() == null ? "": firstTempFtModel.getDataValue25().trim());
            propMap.put("reserved46", firstTempFtModel.getWoId() == null ? "": firstTempFtModel.getWoId().trim());
            propMap.put("reserved55", shipper);
            propMap.put("shipper", shipper);
            if (firstTempFtModel.getDataValue8().equals("1")) {
                propMap.put("holdState", MaterialLot.HOLD_STATE_ON);
                propMap.put("holdReason", firstTempFtModel.getHoldDesc() == null ? "": firstTempFtModel.getHoldDesc().trim());
            }
            if(!StringUtils.isNullOrEmpty(firstTempFtModel.getDataValue12()) && firstTempFtModel.getDataValue12().equals("Y")){
                propMap.put("reserved9", firstTempFtModel.getDataValue12() == null ? "": firstTempFtModel.getDataValue12().trim());
            }
            if(!StringUtils.isNullOrEmpty(firstTempFtModel.getDataValue13()) && firstTempFtModel.getDataValue13().equals("Y")){
                propMap.put("reserved10", firstTempFtModel.getDataValue13() == null ? "": firstTempFtModel.getDataValue13().trim());
            }
            if(!StringUtils.isNullOrEmpty(firstTempFtModel.getDataValue14()) && MaterialLotUnit.PRODUCT_TYPE_ENG.equals(firstTempFtModel.getDataValue14())){
                propMap.put("productType", MaterialLotUnit.PRODUCT_TYPE_ENG);
            }
            propMap.put("packDevice", firstTempFtModel.getPackDevice() == null ? "": firstTempFtModel.getPackDevice().trim());
            propMap.put("materialCode", firstTempFtModel.getMaterialId() == null ? "": firstTempFtModel.getMaterialId().trim());
            propMap.put("vboxQrcodeInfo", firstTempFtModel.getVqrId() == null ? "": firstTempFtModel.getVqrId().trim());
            propMap.put("boxQrcodeInfo", firstTempFtModel.getBqrId() == null ? "": firstTempFtModel.getBqrId().trim());
            propMap.put("sourceProductId", firstTempFtModel.getDataValue29() == null ? "": firstTempFtModel.getDataValue29().trim());
            propMap.put("engineerName", firstTempFtModel.getDataValue3() == null ? "": firstTempFtModel.getDataValue3().trim());
            propMap.put("testPurpose", firstTempFtModel.getDataValue4() == null ? "": firstTempFtModel.getDataValue4().trim());
            propMap.put("workRemarks", firstTempFtModel.getDataValue5() == null ? "": firstTempFtModel.getDataValue5().trim());
            propMap.put("reserved7", productCategory);
            propMap.put("reserved47", fileName);
            propMap.put("reserved48", importCode);
            propMap.put("reserved49", importType);
            propMap.put("reserved50", targetWaferSource);
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(currentQty);
            materialLotAction.setGrade(firstTempFtModel.getGrade());
            materialLotAction.setTransCount(currentSubQty);
            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTargetStorageId(storage.getName());
            materialLotAction.setTargetStorageRrn(storage.getObjectRrn());
            materialLotAction.setStorage(storage);
            materialLotAction.setWarehouse(warehouse);
            materialLotAction.setPropsMap(propMap);

            MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, durable, materialLotAction);

            if(!StringUtils.isNullOrEmpty(shipper)){
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
                materialLotHistoryRepository.save(history);
            }

            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            MaterialLotAction mLotAction = new MaterialLotAction();
            mLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            mLotAction.setTransQty(materialLot.getCurrentQty());
            mLotAction.setResetStorageId("1");
            materialLotActions.add(mLotAction);
            MaterialLot packedLot = packageService.packageMLots(materialLotActions, parentMaterialLotId, "COBPackCase");
            if(!StringUtils.isNullOrEmpty(shipper)){
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
                materialLotHistoryRepository.save(history);
            }
            for (TempFtModel tempFtModel : tempFtModelList) {
                createMaterialLotUnitAndSaveHis(tempFtModel, materialLot);
            }
        } catch (Exception e) {
            result.setResultMessage(e.getMessage() + durable);
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }

    /**
     * 创建晶圆信息并且记录历史
     * @param tempFtModel
     * @param cobMLot
     * @throws ClientException
     */
    private void createMaterialLotUnitAndSaveHis(TempFtModel tempFtModel, MaterialLot cobMLot) throws ClientException{
        try {
            MaterialLotUnit materialLotUnit = new MaterialLotUnit();
            materialLotUnit.setUnitId(tempFtModel.getWaferId().trim());
            materialLotUnit.setMaterial(material);
            materialLotUnit.setMaterialLotRrn(cobMLot.getObjectRrn());
            materialLotUnit.setMaterialLotId(cobMLot.getMaterialLotId());
            materialLotUnit.setLotId(parentMaterialLotId);
            materialLotUnit.setState(MaterialLotUnit.STATE_PACKAGE);
            materialLotUnit.setGrade(cobMLot.getGrade());
            materialLotUnit.setCreated(tempFtModel.getInTime());
            materialLotUnit.setReceiveDate(tempFtModel.getInTime());
            materialLotUnit.setCurrentQty(new BigDecimal(tempFtModel.getWaferNum().trim()));
            materialLotUnit.setReceiveQty(new BigDecimal(tempFtModel.getWaferNum().trim()));
            materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
            materialLotUnit.setPackDevice(cobMLot.getPackDevice());
            materialLotUnit.setEngineerName(cobMLot.getEngineerName());
            materialLotUnit.setWorkRemarks(cobMLot.getWorkRemarks());
            materialLotUnit.setTestPurpose(cobMLot.getTestPurpose());
            materialLotUnit.setProductType(cobMLot.getProductType());
            materialLotUnit.setDurable(cobMLot.getDurable());
            materialLotUnit.setTreasuryNote(tempFtModel.getProdRemarkDesc() == null ? "": tempFtModel.getProdRemarkDesc().trim());
            materialLotUnit.setReserved1(tempFtModel.getSecondCode() == null ? "": tempFtModel.getSecondCode().trim());
            materialLotUnit.setReserved4(tempFtModel.getLocation() == null ? "": tempFtModel.getLocation().trim());
            materialLotUnit.setReserved8(cobMLot.getReserved8());
            materialLotUnit.setReserved13(cobMLot.getReserved13());
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
            unitCreateHistory.setLotId(cobMLot.getLotId());
            materialLotUnitHisRepository.save(unitCreateHistory);

            //晶圆接收历史
            MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
            unitHistory.setCreated(createHisDate);
            unitHistory.setState(MaterialLotUnit.STATE_IN);
            unitCreateHistory.setLotId(cobMLot.getLotId());
            materialLotUnitHisRepository.save(unitHistory);

            //晶圆包装历史
            MaterialLotUnitHistory unitPackHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
            unitPackHistory.setCreated(createHisDate);
            unitPackHistory.setState(MaterialLotUnit.STATE_PACKAGE);
            materialLotUnitHisRepository.save(unitPackHistory);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
