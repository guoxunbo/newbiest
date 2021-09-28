package com.newbiest.vanchip.dto.erp.backhaul.stockin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.base.utils.StringUtils;
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
     * 销售订单行号
     */
    private String Z_BATCH_SOITEM;

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
     * MRB-HOLD 如果客户RMB有值 = H
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
    private String Z_BATCH_MARKING;

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

    /**
     * 封装PO
     */
    private String Z_BATCH_ABNO;

    /**
     * wms批次号
     */
    private String Z_BATCH_WMSBATCH;

    /**
     * 客户销售订单
     */
    private String Z_BATCH_CUSSO;

    /**
     * 客户RMA NO
     */
    private String Z_BATCH_CUSRMA;

    /**
     * 精测RMA NO
     */
    private String Z_BATCH_PLANTRMA;


    /**
     * 超期日期
     */
    private String Z_BATCH_OVERDATE;

    /**
     * 精测MRB
     */
    private String Z_BATCH_MRB2;

    /**
     * PID
     */
    private String Z_BATCH_PID;

    /**
     * Version
     */
    private String Z_BATCH_VERSION2;

    /**
     * 客户MRB（来料MRB）
     */
    private String Z_BATCH_MRB_CODE;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private List<StockInRequestItem> ITEM;

    /**
     * 复制reel信息到requestItem
     * @param materialLot
     * @param requestItem
     * @return
     */
    public StockInRequestItem copyProductMLotToStockInRequestItem(MaterialLot materialLot, StockInRequestItem requestItem){
        requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
        requestItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
        requestItem.setMENGE(materialLot.getCurrentQty());
        requestItem.setMEINS(materialLot.getStoreUom());
        requestItem.setZ_BATCH_CUSPO(materialLot.getReserved6());
        requestItem.setZ_BATCH_SO(materialLot.getReserved7());
        requestItem.setZ_BATCH_CONTROLLOT(materialLot.getReserved4());
        requestItem.setZ_BATCH_DC(materialLot.getReserved9());
        requestItem.setZ_BATCH_MARKING(materialLot.getLetteringInfo());
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
        requestItem.setZ_BATCH_BINTYPE(materialLotUnit.getGrade());
        requestItem.setZ_BATCH_DC(materialLotUnit.getReserved2());
        requestItem.setZ_BATCH_MRB(StringUtils.isNullOrEmpty(materialLotUnit.getReserved5()) ? "" : "H");
        requestItem.setZ_BATCH_MRB_CODE(materialLotUnit.getReserved5());
        requestItem.setZ_BATCH_MRB2(materialLotUnit.getReserved6());
        requestItem.setZ_BATCH_TMACHINE(materialLotUnit.getReserved7());
        requestItem.setZ_BATCH_TPROGRAM(materialLotUnit.getReserved8());
        requestItem.setZ_BATCH_HANDER(materialLotUnit.getReserved9());
        requestItem.setZ_BATCH_INCOMING(materialLotUnit.getReserved10());
        requestItem.setZ_BATCH_CUSTOMER(materialLotUnit.getReserved11());
        requestItem.setZ_BATCH_WMSBATCH(materialLotUnit.getMaterialLotId());
        requestItem.setZ_BATCH_CUSSO(materialLotUnit.getReserved12());
        requestItem.setZ_BATCH_CUSRMA(materialLotUnit.getReserved16());
        requestItem.setZ_BATCH_PLANTRMA(materialLotUnit.getReserved17());
        requestItem.setZ_BATCH_ABNO(materialLotUnit.getReserved18());
        requestItem.setZ_BATCH_REMARK(materialLotUnit.getReserved19());
        requestItem.setZ_BATCH_BONBOOK(materialLotUnit.getReserved20());
        requestItem.setZ_BATCH_MARKING(materialLotUnit.getReserved15());
        requestItem.setZ_BATCH_CUSLOTNO(materialLotUnit.getReserved14());
        requestItem.setZ_BATCH_VERSION2(materialLotUnit.getReserved13());
        requestItem.setZ_BATCH_SO(materialLotUnit.getReserved21());
        requestItem.setZ_BATCH_SOITEM(materialLotUnit.getReserved22());
        return requestItem;
    }
}
