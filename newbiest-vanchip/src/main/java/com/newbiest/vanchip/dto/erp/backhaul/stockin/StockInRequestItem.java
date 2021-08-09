package com.newbiest.vanchip.dto.erp.backhaul.stockin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class StockInRequestItem implements Serializable {

    /**
     * 数量
     */
    private BigDecimal MENGE;

    /**
     * 基本计量单位
     */
    private String MEINS;

    /**
     * 入库日期
     */
    private String Z_BATCH_POSTEDATE;

    /**
     * 客户订单号码
     */
    private String Z_BATCH_CUSPO;

    /**
     * 销售订单号
     */
    private String Z_BATCH_SO;

    /**
     * 内部生产订单
     */
    private String Z_BATCH_INTERORDOR;

    /**
     * CONTROL LOT
     */
    private String Z_BATCH_CONTROLLOT;

    /**
     * 测试批次
     */
    private String Z_BATCH_TBATCH;

    /**
     * 内盒号
     */
    private String Z_BATCH_REEL;

    /**
     * MRB
     */
    private String Z_BATCH_MRB;

    /**
     * bin别
     */
    private String Z_BATCH_BINTYPE;

    /**
     * DC
     */
    private String Z_BATCH_DC;

    /**
     * 印次信息
     */
    private String Z_BATCH_MARING;

    /**
     * 测试机台
     */
    private String Z_BATCH_TMACHINE;

    /**
     * 测试程序
     */
    private String Z_BATCH_TPROGRAM;

    /**
     * handler 型号
     */
    private String Z_BATCH_HANDER;

    /**
     * packing date
     */
    private String Z_BATCH_PDATE;

    /**
     * 来料工厂
     */
    private String Z_BATCH_INCOMING;

    /**
     * 客户
     */
    private String Z_BATCH_CUSTOMER;

    /**
     * Remark
     */
    private String Z_BATCH_REMARK;

    /**
     * 保税手册
     */
    private String Z_BATCH_BONBOOK;

    /**
     * customer lot no
     */
    private String Z_BATCH_CUSLOTNO;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private List<StockInRequestItem> ITEM;

    /**
     * 复制主材批次信息到requestItem
     * @param materialLot
     * @param requestItem
     * @return
     */
    public StockInRequestItem copyMainMaterialLotToStockInRequestItem(MaterialLot materialLot, StockInRequestItem requestItem){
        requestItem.setMENGE(materialLot.getCurrentQty());
        requestItem.setMEINS(materialLot.getStoreUom());
        requestItem.setZ_BATCH_CUSPO(materialLot.getReserved6());
        requestItem.setZ_BATCH_SO(materialLot.getReserved7());
        requestItem.setZ_BATCH_INTERORDOR(materialLot.getWorkOrderId());
        requestItem.setZ_BATCH_CONTROLLOT(materialLot.getReserved4());
//        requestItem.setZ_BATCH_TBATCH("测试批次");
//        requestItem.setZ_BATCH_REEL("REEL");
//        requestItem.setZ_BATCH_BINTYPE("BIN");
        requestItem.setZ_BATCH_DC(materialLot.getReserved9());
        requestItem.setZ_BATCH_MARING(materialLot.getLetteringInfo());
//        requestItem.setZ_BATCH_TMACHINE("测试机台");
//        requestItem.setZ_BATCH_TPROGRAM("测试程序");
//        requestItem.setZ_BATCH_HANDER("handler型号");
//        requestItem.setZ_BATCH_PDATE("packing date");
//        requestItem.setZ_BATCH_INCOMING("来料工厂");
//        requestItem.setZ_BATCH_CUSTOMER("客户");
        requestItem.setZ_BATCH_REMARK(materialLot.getIncomingComment());
        requestItem.setZ_BATCH_BONBOOK(materialLot.getReserved19());
        requestItem.setZ_BATCH_CUSLOTNO(materialLot.getReserved5());
        return requestItem;
    }

    /**
     * 复制reel信息到requestItem
     * @param materialLot
     * @param requestItem
     * @return
     */
    public StockInRequestItem copyProductMLotToStockInRequestItem(MaterialLot materialLot, StockInRequestItem requestItem){
        requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
        requestItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
        //日期需转换
//        requestItem.setZ_BATCH_PDATE(materialLot.getProductionDate());
//        requestItem.setMENGE(materialLot.getCurrentQty());
//        requestItem.setMEINS(materialLot.getStoreUom());
//        requestItem.setZ_BATCH_CUSPO("客户订单号");
//        requestItem.setZ_BATCH_SO("销售订单号");
//        requestItem.setZ_BATCH_INTERORDOR("内部生产订单");
//        requestItem.setZ_BATCH_CONTROLLOT("control lot");
//        requestItem.setZ_BATCH_TBATCH("测试批次");
//        requestItem.setZ_BATCH_DC("dc");
//        requestItem.setZ_BATCH_MARING("maring");
//        requestItem.setZ_BATCH_TMACHINE("测试机台");
//        requestItem.setZ_BATCH_TPROGRAM("测试程序");
//        requestItem.setZ_BATCH_HANDER("handler型号");

//        requestItem.setZ_BATCH_INCOMING("来料工厂");
//        requestItem.setZ_BATCH_CUSTOMER("客户");
//        requestItem.setZ_BATCH_REMARK("remark");
//        requestItem.setZ_BATCH_BONBOOK("保税手册");
        return requestItem;
    }

    /**
     * 复制测试批次信息到requestItem
     * @param materialLotUnit
     * @param requestItem
     * @return
     */
    public StockInRequestItem copyMLotUnitToStockInRequestItem(MaterialLotUnit materialLotUnit, StockInRequestItem requestItem){
        requestItem.setMENGE(materialLotUnit.getQty());
        requestItem.setMEINS(materialLotUnit.getStoreUom());
        requestItem.setZ_BATCH_CUSPO(materialLotUnit.getReserved1());
        requestItem.setZ_BATCH_INTERORDOR(materialLotUnit.getWorkOrderId());
        requestItem.setZ_BATCH_CONTROLLOT(materialLotUnit.getReserved4());
        requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
        requestItem.setZ_BATCH_REEL(materialLotUnit.getMaterialLotId());
        requestItem.setZ_BATCH_BINTYPE(materialLotUnit.getGrade());
        requestItem.setZ_BATCH_DC(materialLotUnit.getReserved2());

//        requestItem.setZ_BATCH_SO("销售订单号");
//        requestItem.setZ_BATCH_MARING("maring");
//        requestItem.setZ_BATCH_TMACHINE("测试机台");
//        requestItem.setZ_BATCH_TPROGRAM("测试程序");
//        requestItem.setZ_BATCH_HANDER("handler型号");
//        requestItem.setZ_BATCH_PDATE("packing date");
//        requestItem.setZ_BATCH_INCOMING("来料工厂");
//        requestItem.setZ_BATCH_CUSTOMER("客户");
//        requestItem.setZ_BATCH_REMARK("remark");
//        requestItem.setZ_BATCH_BONBOOK("保税手册");
        return requestItem;
    }
}
