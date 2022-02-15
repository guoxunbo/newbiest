package com.newbiest.mms.thread;

import com.google.common.collect.Maps;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.model.MaterialLotUnitHistory;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
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
public class ImportMLotThread implements Callable {

    /**
     * 物料批次别名
     * 可以理解成durable对应的批次号
     */
    private String lotId;
    private String materialLotId;
    private String importCode;
    private String productType;
    private List<MaterialLotUnit> materialLotUnits;
    private Material material;
    private StatusModel statusModel;

    private MmsService mmsService;
    private BaseService baseService;
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

            BigDecimal totalQty = materialLotUnits.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotUnit :: getCurrentQty));
            BigDecimal currentSubQty = new BigDecimal(materialLotUnits.size());
            Map<String, Object> propsMap = Maps.newHashMap();
            propsMap.put("category", MaterialLot.CATEGORY_UNIT);
            if(!StringUtils.isNullOrEmpty(materialLotUnits.get(0).getDurable())){
                propsMap.put("durable", materialLotUnits.get(0).getDurable().toUpperCase());
            }
            propsMap.put("supplier", materialLotUnits.get(0).getSupplier());
            propsMap.put("shipper", materialLotUnits.get(0).getShipper());
            propsMap.put("grade", materialLotUnits.get(0).getGrade());
            propsMap.put("lotId", lotId.toUpperCase());
            propsMap.put("sourceProductId", materialLotUnits.get(0).getSourceProductId());

            String subCode = materialLotUnits.get(0).getReserved1();
            if(MaterialLot.IMPORT_WLA.equals(materialLotUnits.get(0).getReserved49()) && !StringUtils.isNullOrEmpty(subCode) && subCode.length() == 3){
                subCode = subCode + lotId.substring(0,1);
            }
            propsMap.put("reserved1",subCode);
            propsMap.put("reserved4",materialLotUnits.get(0).getTreasuryNote());
            propsMap.put("reserved6",materialLotUnits.get(0).getReserved4());
            propsMap.put("reserved7",materialLotUnits.get(0).getReserved7());
            propsMap.put("reserved13",materialLotUnits.get(0).getReserved13());
            propsMap.put("reserved14",materialLotUnits.get(0).getReserved14());
            propsMap.put("reserved22",materialLotUnits.get(0).getReserved22());
            propsMap.put("reserved23",materialLotUnits.get(0).getReserved23());
            propsMap.put("reserved24",materialLotUnits.get(0).getReserved24());
            String lotType = materialLotUnits.get(0).getReserved25();
            if (!StringUtils.isNullOrEmpty(lotType) && MaterialLotUnit.STRING_NULL.equals(lotType.toUpperCase().trim())){
                lotType = StringUtils.EMPTY;
            } else {
                lotType = materialLotUnits.get(0).getReserved25();
            }
            propsMap.put("reserved25",lotType);
            propsMap.put("reserved26",materialLotUnits.get(0).getReserved26());
            propsMap.put("reserved27",materialLotUnits.get(0).getReserved27());
            propsMap.put("reserved28",materialLotUnits.get(0).getReserved28());
            propsMap.put("reserved29",materialLotUnits.get(0).getReserved29());
            propsMap.put("reserved32",materialLotUnits.get(0).getReserved32());
            propsMap.put("reserved33",materialLotUnits.get(0).getReserved33());
            propsMap.put("reserved34",materialLotUnits.get(0).getReserved34());
            propsMap.put("reserved35",materialLotUnits.get(0).getReserved35());
            propsMap.put("reserved36",materialLotUnits.get(0).getReserved36());
            propsMap.put("reserved37",materialLotUnits.get(0).getReserved37());
            propsMap.put("reserved38",materialLotUnits.get(0).getReserved38());
            propsMap.put("reserved39",materialLotUnits.get(0).getReserved39());
            propsMap.put("reserved40",materialLotUnits.get(0).getReserved40());
            propsMap.put("reserved41",materialLotUnits.get(0).getReserved41());
            propsMap.put("reserved45",materialLotUnits.get(0).getReserved45());
            propsMap.put("reserved46",materialLotUnits.get(0).getReserved46());
            propsMap.put("reserved47",materialLotUnits.get(0).getReserved47());
            propsMap.put("reserved49",materialLotUnits.get(0).getReserved49());
            propsMap.put("reserved50",materialLotUnits.get(0).getReserved50());
            propsMap.put("reserved48",importCode);
            propsMap.put("productType",productType);

            MaterialLotAction materialLotAction = new MaterialLotAction(materialLotId, StringUtils.EMPTY, propsMap, totalQty, currentSubQty, StringUtils.EMPTY);
            MaterialLot materialLot = mmsService.createMLot(material, statusModel, materialLotAction);

            if (MaterialLot.IMPORT_LCD_CP.equals(materialLot.getReserved49()) || MaterialLot.IMPORT_SENSOR_CP.equals(materialLot.getReserved49())) {
                mmsService.validateFutureHoldByReceiveTypeAndProductAreaAndLotId(MaterialLot.GC_INCOMING_MATERIAL_IMPORT, materialLot.getReserved49(), materialLot.getLotId());
            }
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                if (MaterialLot.IMPORT_WLA.equals(materialLotUnit.getReserved49()) || MaterialLot.IMPORT_LCD_CP.equals(materialLot.getReserved49())
                        || MaterialLot.IMPORT_SENSOR_CP.equals(materialLot.getReserved49())) {
                    mmsService.validateFutureHoldByWaferId(materialLotUnit.getUnitId(), materialLot);
                }
                if(!StringUtils.isNullOrEmpty(materialLotUnit.getDurable())){
                    materialLotUnit.setDurable(materialLotUnit.getDurable().toUpperCase());
                }
                materialLotUnit.setLotId(materialLotUnit.getLotId().toUpperCase());
                materialLotUnit.setUnitId(materialLotUnit.getUnitId().toUpperCase());//晶圆号小写转大写
                materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotUnit.setLotId(materialLot.getLotId());
                materialLotUnit.setReserved1(materialLot.getReserved1());
                materialLotUnit.setReceiveQty(materialLotUnit.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved18("0");
                materialLotUnit.setReserved25(materialLot.getReserved25());
                materialLotUnit.setReserved6(StringUtils.EMPTY);//来料导入时reserved6不是报税属性，暂时清空
                materialLotUnit.setReserved7(StringUtils.EMPTY);//晶圆信息不保存产品型号
                materialLotUnit.setReserved48(importCode);
                materialLotUnit.setMaterial(material);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                materialLotUnitArrayList.add(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                history.setTransQty(materialLotUnit.getReceiveQty());
                materialLotUnitHisRepository.save(history);
            }
            result.setMaterialLotUnits(materialLotUnits);
        } catch (Exception e) {
            result.setResultMessage(e.getMessage());
            result.setResult(ResponseHeader.RESULT_FAIL);
        } finally {
            ThreadLocalContext.remove();
        }

        return result;
    }
}
