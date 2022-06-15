package com.newbiest.mms.thread;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.utils.CollectorsUtils;
import com.newbiest.msg.ResponseHeader;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 导入物料批次的线程
 * 返回一个Callable结果
 * @author guoxunbo
 * @date 2020-10-15 14:21
 */
@Data
public class ImportCobMLotThread implements Callable {

    private String lotId;
    private String materialLotId;
    private String parentMaterialLotId;
    private String importCode;
    private String productType;
    private List<MaterialLotUnit> materialLotUnitList;
    private Material material;
    private StatusModel statusModel;

    private MmsService mmsService;
    private BaseService baseService;
    private PackageService packageService;
    private MaterialLotRepository materialLotRepository;
    private MaterialLotUnitRepository materialLotUnitRepository;
    private MaterialLotUnitHisRepository materialLotUnitHisRepository;
    private SessionContext sessionContext;

    @Override
    public ImportMLotThreadResult call()  {
        // 涉及到父子线程进行传递ThreadLocal。但是GC依赖的core为1.0.4故没有实现父子线程之间ThreadLocal的共享。故在线程内部进行再次各自进行put
        ThreadLocalContext.putSessionContext(sessionContext);
        ImportMLotThreadResult result = new ImportMLotThreadResult();
        try {
            List<MaterialLotUnit> materialLotUnitArrayList = new ArrayList<>();

            BigDecimal totalQty = materialLotUnitList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotUnit :: getCurrentQty));
            BigDecimal currentSubQty = new BigDecimal(materialLotUnitList.size());
            String location = materialLotUnitList.get(0).getReserved4();
            Map<String, Object> propsMap = Maps.newHashMap();
            propsMap.put("category", MaterialLot.CATEGORY_UNIT);
            propsMap.put("durable", lotId.toUpperCase());
            propsMap.put("supplier", materialLotUnitList.get(0).getSupplier());
            propsMap.put("shipper", materialLotUnitList.get(0).getShipper());
            propsMap.put("grade", materialLotUnitList.get(0).getGrade());
            propsMap.put("lotId", lotId.toUpperCase());
            propsMap.put("reserved1",materialLotUnitList.get(0).getReserved1());
            propsMap.put("reserved4",materialLotUnitList.get(0).getTreasuryNote());
            propsMap.put("reserved6",location);
            propsMap.put("reserved7",materialLotUnitList.get(0).getReserved7());
            if(MaterialLot.BONDED_PROPERTY_ZSH.equals(location)){
                propsMap.put("reserved14", MaterialLotInventory.ZSH_DEFAULT_STORAGE_ID);
            } else if(MaterialLot.LOCATION_SH.equals(location)){
                propsMap.put("reserved14", MaterialLotInventory.SH_DEFAULT_STORAGE_ID);
            }  else {
                propsMap.put("reserved14", materialLotUnitList.get(0).getReserved14());
            }
            propsMap.put("reserved13", materialLotUnitList.get(0).getReserved13());
            propsMap.put("reserved22", materialLotUnitList.get(0).getReserved22());
            propsMap.put("reserved23", materialLotUnitList.get(0).getReserved23());
            propsMap.put("reserved24", materialLotUnitList.get(0).getReserved24());
            String lotType = materialLotUnitList.get(0).getReserved25();
            if (!StringUtils.isNullOrEmpty(lotType) && MaterialLotUnit.STRING_NULL.equals(lotType.toUpperCase().trim())){
                lotType = StringUtils.EMPTY;
            } else {
                lotType = materialLotUnitList.get(0).getReserved25();
            }
            propsMap.put("reserved25", lotType);
            propsMap.put("reserved26", materialLotUnitList.get(0).getReserved26());
            propsMap.put("reserved27", materialLotUnitList.get(0).getReserved27());
            propsMap.put("reserved28", materialLotUnitList.get(0).getReserved28());
            propsMap.put("reserved29", materialLotUnitList.get(0).getReserved29());
            propsMap.put("reserved32", materialLotUnitList.get(0).getReserved32());
            propsMap.put("reserved33", materialLotUnitList.get(0).getReserved33());
            propsMap.put("reserved34", materialLotUnitList.get(0).getReserved34());
            propsMap.put("reserved35", materialLotUnitList.get(0).getReserved35());
            propsMap.put("reserved36", materialLotUnitList.get(0).getReserved36());
            propsMap.put("reserved37", materialLotUnitList.get(0).getReserved37());
            propsMap.put("reserved38", materialLotUnitList.get(0).getReserved38());
            propsMap.put("reserved39", materialLotUnitList.get(0).getReserved39());
            propsMap.put("reserved40", materialLotUnitList.get(0).getReserved40());
            propsMap.put("reserved41", materialLotUnitList.get(0).getReserved41());
            propsMap.put("reserved45", materialLotUnitList.get(0).getReserved45());
            propsMap.put("reserved46", materialLotUnitList.get(0).getReserved46());
            propsMap.put("reserved47", materialLotUnitList.get(0).getReserved47());
            propsMap.put("reserved49", materialLotUnitList.get(0).getReserved49());
            propsMap.put("reserved50", materialLotUnitList.get(0).getReserved50());
            propsMap.put("reserved48", importCode);

            MaterialLotAction materialLotAction = new MaterialLotAction(materialLotId, StringUtils.EMPTY, propsMap, totalQty, currentSubQty, StringUtils.EMPTY);
            MaterialLot materialLot = mmsService.createMLot(material, statusModel, materialLotAction);

            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            MaterialLotAction mLotAction = new MaterialLotAction();
            mLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            mLotAction.setTransQty(materialLot.getCurrentQty());
            mLotAction.setCobImportPack("1");
            materialLotActions.add(mLotAction);
            packageService.packageMLots(materialLotActions, parentMaterialLotId, "COBPackCase");

            for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                materialLotUnit.setLotId(parentMaterialLotId);
                materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotUnit.setDurable(materialLot.getLotId());
                materialLotUnit.setUnitId(materialLotUnit.getUnitId().toUpperCase());
                materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                materialLotUnit.setReceiveDate(materialLot.getReceiveDate());
                materialLotUnit.setReserved1(materialLot.getReserved1());
                materialLotUnit.setReceiveQty(materialLotUnit.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved14(materialLot.getReserved14());
                materialLotUnit.setReserved18("0");
                materialLotUnit.setReserved25(materialLot.getReserved25());
                materialLotUnit.setReserved6(StringUtils.EMPTY);
                materialLotUnit.setReserved7(StringUtils.EMPTY);
                materialLotUnit.setReserved48(importCode);
                materialLotUnit.setMaterial(material);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                materialLotUnitArrayList.add(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                history.setTransQty(materialLotUnit.getReceiveQty());
                materialLotUnitHisRepository.save(history);
            }
            result.setMaterialLotUnits(materialLotUnitArrayList);
        } catch (Exception e) {
            result.setResultMessage(e.getMessage());
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }
        return result;
    }
}
