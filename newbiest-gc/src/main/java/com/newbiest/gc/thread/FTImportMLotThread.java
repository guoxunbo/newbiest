package com.newbiest.gc.thread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 导入物料批次的线程
 * 返回一个Callable结果
 * @author guozhangLuo
 * @date 2022-05-26 14:21
 */
@Data
@Slf4j
public class FTImportMLotThread implements Callable {

    /**
     * 物料批次别名
     * 可以理解成durable对应的批次号
     */
    private String parentMaterialLotId;
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
            for(TempFtModel vboxMLot : tempFtModelList){
                BigDecimal currentQty = new BigDecimal(vboxMLot.getWaferNum());
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(currentQty);
                materialLotAction.setGrade(vboxMLot.getGrade());
                materialLotAction.setTargetStorageRrn(storage.getObjectRrn());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setStorage(storage);
                materialLotAction.setWarehouse(warehouse);
                materialLotAction.setTargetStorageId(vboxMLot.getPointId());

                Map<String, Object> propMap = Maps.newConcurrentMap();
                if(!StringUtils.isNullOrEmpty(vboxMLot.getStockId())){
                    propMap.put("reserved13", materialLotAction.getTargetWarehouseRrn().toString());
                }
                propMap.put("reserved14", vboxMLot.getPointId() == null ? "": vboxMLot.getPointId().trim());
                if(!StringUtils.isNullOrEmpty(vboxMLot.getBoxId()) && !vboxMLot.getBoxId().startsWith(TempFtModel.BOX_START_B) &&
                        !vboxMLot.getBoxId().startsWith(TempFtModel.BOX_START_SBB) && !vboxMLot.getBoxId().startsWith(TempFtModel.BOX_START_LB) &&
                        !vboxMLot.getBoxId().startsWith(TempFtModel.BOX_START_BZZSH)){
                    propMap.put("reserved8", vboxMLot.getBoxId() == null ? "": vboxMLot.getBoxId().trim());
                }
                propMap.put("created", vboxMLot.getInTime());
                propMap.put("receiveDate", vboxMLot.getInTime());
                propMap.put("reserved1", vboxMLot.getSecondCode() == null ? "": vboxMLot.getSecondCode().trim());
                propMap.put("reserved3", vboxMLot.getSaleRemarkDesc() == null ? "": vboxMLot.getSaleRemarkDesc().trim());
                propMap.put("reserved4", vboxMLot.getProdRemarkDesc() == null ? "": vboxMLot.getProdRemarkDesc().trim());
                propMap.put("reserved6", vboxMLot.getLocation() == null ? "" : vboxMLot.getLocation().trim());
                propMap.put("reserved22", vboxMLot.getVendor() == null ? "": vboxMLot.getVendor().trim());
                propMap.put("reserved24", vboxMLot.getFabDevice() == null ? "": vboxMLot.getFabDevice().trim());
                propMap.put("reserved27", vboxMLot.getPoNo() == null ? "": vboxMLot.getPoNo().trim());
                propMap.put("reserved29", vboxMLot.getInvoiceId() == null ? "": vboxMLot.getInvoiceId().trim());
                propMap.put("reserved32", vboxMLot.getWaferNum() == null ? "": vboxMLot.getWaferNum().trim());
                propMap.put("reserved33", vboxMLot.getDataValue19() == null ? "": vboxMLot.getDataValue19().trim());
                propMap.put("reserved34", vboxMLot.getPassNum() == null ? "": vboxMLot.getPassNum().trim());
                propMap.put("reserved35", vboxMLot.getNgNum() == null ? "": vboxMLot.getNgNum().trim());
                propMap.put("reserved36", vboxMLot.getYield() == null ? "": vboxMLot.getYield().trim());
                propMap.put("reserved37", vboxMLot.getPackLotId() == null ? "": vboxMLot.getPackLotId().trim());
                propMap.put("reserved38", vboxMLot.getDataValue16() == null ? "": vboxMLot.getDataValue16().trim());
                propMap.put("reserved39", vboxMLot.getCartonNo() == null ? "": vboxMLot.getCartonNo().trim());
                propMap.put("reserved41", vboxMLot.getRemark() == null ? "": vboxMLot.getRemark().trim());
                propMap.put("reserved42", vboxMLot.getDataValue20() == null ? "": vboxMLot.getDataValue20().trim());
                propMap.put("reserved43", vboxMLot.getDataValue24() == null ? "": vboxMLot.getDataValue24().trim());
                propMap.put("reserved45", vboxMLot.getDataValue25() == null ? "": vboxMLot.getDataValue25().trim());
                propMap.put("reserved46", vboxMLot.getWoId() == null ? "": vboxMLot.getWoId().trim());
                propMap.put("reserved47", fileName);
                propMap.put("reserved7", productCategory);
                propMap.put("reserved48", importCode);
                propMap.put("reserved49", importType);
                propMap.put("reserved50", targetWaferSource);

                if (vboxMLot.getDataValue8().equals("1")) {
                    propMap.put("holdState", MaterialLot.HOLD_STATE_ON);
                    propMap.put("holdReason", vboxMLot.getHoldDesc() == null ? "": vboxMLot.getHoldDesc().trim());
                }
                if(!StringUtils.isNullOrEmpty(vboxMLot.getDataValue12()) && vboxMLot.getDataValue12().equals("Y")){
                    propMap.put("reserved9", vboxMLot.getDataValue12() == null ? "": vboxMLot.getDataValue12().trim());
                }
                if(!StringUtils.isNullOrEmpty(vboxMLot.getDataValue13()) && vboxMLot.getDataValue13().equals("Y")){
                    propMap.put("reserved10", vboxMLot.getDataValue13() == null ? "": vboxMLot.getDataValue13().trim());
                }
                if(!StringUtils.isNullOrEmpty(vboxMLot.getDataValue14()) && MaterialLotUnit.PRODUCT_TYPE_ENG.equals(vboxMLot.getDataValue14())){
                    propMap.put("productType", MaterialLotUnit.PRODUCT_TYPE_ENG);
                }
                propMap.put("packDevice", vboxMLot.getPackDevice() == null ? "": vboxMLot.getPackDevice().trim());
                propMap.put("materialCode", vboxMLot.getMaterialId() == null ? "": vboxMLot.getMaterialId().trim());
                propMap.put("vboxQrcodeInfo", vboxMLot.getVqrId() == null ? "": vboxMLot.getVqrId().trim());
                propMap.put("boxQrcodeInfo", vboxMLot.getBqrId() == null ? "": vboxMLot.getBqrId().trim());
                propMap.put("sourceProductId", vboxMLot.getDataValue29() == null ? "": vboxMLot.getDataValue29().trim());
                propMap.put("engineerName", vboxMLot.getDataValue3() == null ? "": vboxMLot.getDataValue3().trim());
                propMap.put("testPurpose", vboxMLot.getDataValue4() == null ? "": vboxMLot.getDataValue4().trim());
                propMap.put("workRemarks", vboxMLot.getDataValue5() == null ? "": vboxMLot.getDataValue5().trim());

                materialLotAction.setPropsMap(propMap);
                MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, vboxMLot.getWaferId(), materialLotAction);
            }

            Set<String> vboxIdList = tempFtModelList.stream().map(TempFtModel::getWaferId).collect(Collectors.toSet());
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            for (String boxedLotId : vboxIdList) {
                MaterialLot materialLot = mmsService.getMLotByMLotId(boxedLotId);
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setResetStorageId("1");
                materialLotActions.add(materialLotAction);
            }

            MaterialLot packedMLot = packageService.packageMLots(materialLotActions, parentMaterialLotId, packageType);

            //检验箱中是否存在已经做过出货检验的真空包
            List<TempFtModel> checkOutList = tempFtModelList.stream().filter(tempFtModel -> !StringUtils.isNullOrEmpty(tempFtModel.getDataValue13()) && tempFtModel.getDataValue13().equals("Y")).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(checkOutList)){
                packedMLot.setReserved9("PASS");
                packedMLot = mmsService.changeMaterialLotState(packedMLot, "OQC", "OK");
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMLot, "OQC");
                history.setCreated(createHisDate);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            result.setResultMessage(e.getMessage() + parentMaterialLotId);
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }
}
