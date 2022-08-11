package com.newbiest.mms.thread;

import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.PackagedLotDetailRepository;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.msg.ResponseHeader;
import lombok.Data;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 导入LCD成品物料批次的线程
 * 返回一个Callable结果
 * @author guozhangLuo
 * @date 2022-08-01 14:21
 */
@Data
public class ImportLcdMLotThread implements Callable {

    private String parentMaterialLotId;
    private String importCode;
    private List<MaterialLot> materialLotList;
    private Material material;
    private MaterialLot packedMaterialLot;
    private Integer totalQty;

    private BaseService baseService;
    private PackageService packageService;
    private MaterialLotRepository materialLotRepository;
    private MaterialLotHistoryRepository materialLotHistoryRepository;
    private PackagedLotDetailRepository packagedLotDetailRepository;
    private SessionContext sessionContext;

    @Override
    public ImportMLotThreadResult call()  {
        ThreadLocalContext.putSessionContext(sessionContext);
        ImportMLotThreadResult result = new ImportMLotThreadResult();
        try {
            for(MaterialLot materialLot : materialLotList){
                materialLot.setMaterial(material);
                materialLot.setReserved2("N");
                materialLot.initialMaterialLot();
                materialLot.setStatusModelRrn(material.getStatusModelRrn());
                materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COG);
                materialLot.setReserved14(packedMaterialLot.getReserved14());
                materialLot.setReserved48(importCode);
                materialLot.setReserved49(MaterialLot.IMPORT_COG);
                materialLot.setReserved50("17");
                materialLot.setParentMaterialLotId(parentMaterialLotId);
                materialLot.setParentMaterialLotRrn(packedMaterialLot.getObjectRrn());
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                history.setParentMaterialLotId(null);
                history.setParentMaterialLotRrn(null);
                materialLotHistoryRepository.save(history);

                MaterialLotHistory packedLotHis = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE);
                materialLotHistoryRepository.save(packedLotHis);

                PackagedLotDetail packagedLotDetail = new PackagedLotDetail();
                packagedLotDetail.setPackagedLotRrn(packedMaterialLot.getObjectRrn());
                packagedLotDetail.setPackagedLotId(parentMaterialLotId);
                packagedLotDetail.setMaterialLotRrn(materialLot.getObjectRrn());
                packagedLotDetail.setMaterialLotId(materialLot.getMaterialLotId());
                packagedLotDetail.setQty(materialLot.getCurrentQty());
                packagedLotDetailRepository.saveAndFlush(packagedLotDetail);
            }
        } catch (Exception e) {
            result.setResultMessage(e.getMessage());
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }
}
