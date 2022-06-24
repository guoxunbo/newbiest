package com.newbiest.gc.thread;

import com.google.common.collect.Maps;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.scm.dto.TempFtModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.msg.ResponseHeader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 导入物料批次的线程
 * 返回一个Callable结果
 * @author guozhangLuo
 * @date 2022-06-06 14:21
 */
@Data
@Slf4j
public class FTImportVBoxThread implements Callable {

    private String importCode;
    private String productCategory;
    private String importType;
    private String targetWaferSource;
    private String fileName;
    private Material material;
    private Date createHisDate;
    private Warehouse warehouse;
    private Storage storage;
    private TempFtModel tempFtModel;

    private MmsService mmsService;
    private BaseService baseService;
    private PackageService packageService;
    private MaterialLotRepository materialLotRepository;
    private MaterialLotHistoryRepository materialLotHistoryRepository;
    private SessionContext sessionContext;

    @Override
    public FTImportMLotThreadResult call()  {
        ThreadLocalContext.putSessionContext(sessionContext);
        FTImportMLotThreadResult result = new FTImportMLotThreadResult();
        try {
            BigDecimal currentQty = new BigDecimal(tempFtModel.getWaferNum());
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(currentQty);
            materialLotAction.setGrade(tempFtModel.getGrade());
            materialLotAction.setTargetStorageRrn(storage.getObjectRrn());
            materialLotAction.setStorage(storage);
            materialLotAction.setWarehouse(warehouse);
            if (!StringUtils.isNullOrEmpty(tempFtModel.getStockId())) {
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTargetStorageId(tempFtModel.getPointId());
            }

            Map<String, Object> propMap = Maps.newConcurrentMap();
            propMap.put("reserved13", warehouse.getObjectRrn().toString());
            propMap.put("reserved14", tempFtModel.getPointId() == null ? "": tempFtModel.getPointId().trim());
            propMap.put("reserved8", tempFtModel.getBoxId() == null ? "": tempFtModel.getBoxId().trim());
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
            propMap.put("reserved32", tempFtModel.getWaferNum() == null ? "": tempFtModel.getWaferNum().trim());
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
            propMap.put("reserved7", productCategory);
            propMap.put("reserved48", importCode);
            propMap.put("reserved49", importType);
            propMap.put("reserved50", targetWaferSource);

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
            propMap.put("packDevice", tempFtModel.getPackDevice() == null ? "": tempFtModel.getPackDevice().trim());
            propMap.put("materialCode", tempFtModel.getMaterialId() == null ? "": tempFtModel.getMaterialId().trim());
            propMap.put("vboxQrcodeInfo", tempFtModel.getVqrId() == null ? "": tempFtModel.getVqrId().trim());
            propMap.put("boxQrcodeInfo", tempFtModel.getBqrId() == null ? "": tempFtModel.getBqrId().trim());
            propMap.put("sourceProductId", tempFtModel.getDataValue29() == null ? "": tempFtModel.getDataValue29().trim());
            propMap.put("engineerName", tempFtModel.getDataValue3() == null ? "": tempFtModel.getDataValue3().trim());
            propMap.put("testPurpose", tempFtModel.getDataValue4() == null ? "": tempFtModel.getDataValue4().trim());
            propMap.put("workRemarks", tempFtModel.getDataValue5() == null ? "": tempFtModel.getDataValue5().trim());

            materialLotAction.setPropsMap(propMap);
            MaterialLot unPackVbox = mmsService.receiveMLot2Warehouse(material, tempFtModel.getWaferId(), materialLotAction);

            if (!StringUtils.isNullOrEmpty(unPackVbox.getReserved10())) {
                unPackVbox.setReserved9("PASS");
                unPackVbox = mmsService.changeMaterialLotState(unPackVbox, "OQC", "OK");
                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(unPackVbox, "OQC");
                materialLotHistory.setCreated(createHisDate);
                materialLotHistoryRepository.save(materialLotHistory);
            }

        } catch (Exception e) {
            result.setResultMessage(e.getMessage() + tempFtModel.getWaferId());
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }
}
