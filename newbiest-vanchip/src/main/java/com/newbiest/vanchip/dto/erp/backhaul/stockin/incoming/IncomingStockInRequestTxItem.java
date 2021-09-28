package com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingStockInRequestTxItem implements Serializable {

    /**
     * PO号
     */
    private String Z_BATCH_PONUMBER;

    /**
     * remark
     */
    private String Z_BATCH_REMARK;

    /**
     * reel号
     */
    private String Z_BATCH_REEL;

    /**
     * DC
     */
    private String Z_BATCH_DC;

    /**
     * Marking
     */
    private String Z_BATCH_MARING;

    /**
     * big别
     */
    private String Z_BATCH_BINTYPE;

    /**
     * PO号
     */
    private String Z_BATCH_CUSPO;

    /**
     * 销售订单号
     */
    private String Z_BATCH_SO;

    /**
     * 销售订单行项目
     */
    private String Z_BATCH_SOITEM;

    /**
     * 内部生产订单
     */
    private String Z_BATCH_INTERORDOR;

    /**
     * BOX_NO
     */
    private String Z_BATCH_BOXNO;

    /**
     * CONTROL LOT
     */
    private String Z_BATCH_CONTROLLOT;

    /**
     * CUSTOMER LOT NO
     */
    private String Z_BATCH_CUSLOTNO;

    /**
     * 客户
     */
    private String Z_BATCH_CUSTUMER;

    /**
     * 来料工厂
     */
    private String Z_BATCH_INCOMING;

    /**
     * MRB
     */
    private String Z_BATCH_MRB_CODE;

    /**
     * 测试批次
     */
    private String Z_BATCH_TBATCH;

    /**
     * IQC检验结果
     */
    private String Z_BATCH_IQC;

    /**
     * 保税手册号
     */
    private String Z_BATCH_BONBOOK;

    /**
     * 外箱号
     */
    private String Z_BATCH_CTNNO;

    /**
     * 精测MRB
     */
    private String Z_BATCH_MRB2;

    /**
     * 封装PO
     */
    private String Z_BATCH_ABNO;

    /**
     *WMS批次号
     */
    private String Z_BATCH_WMSBATCH;

    /**
     * 来料单
     */
    private String Z_BATCH_GFNO;

    /**
     * 生产日期
     */
    private String Z_BATCH_MADATE;

    /**
     * 超期日期
     */
    private String Z_BATCH_OVERDATE;

    /**
     * 入库日期
     */
    private String Z_BATCH_POSTEDATE;

    /**
     * 精测RMA
     */
    private String Z_BATCH_PLANTRMA;

    /**
     * 客户RMA
     */
    private String Z_BATCH_CUSRMA;

    /**
     * 客户销售订单号
     */
    private String Z_BATCH_CUSSO;

    /**
     * 客供料接收时间
     */
    private String Z_BATCH_RC;

    /**
     * 版本
     */
    private String Z_BATCH_VERSION2;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    public IncomingStockInRequestTxItem copyMaterialLotToRequestTxItem(MaterialLot materialLot, IncomingStockInRequestTxItem requestTxItem){
        //requestTxItem.setZ_BATCH_PONUMBER(materialLot.getReserved6());
        requestTxItem.setZ_BATCH_CUSPO(materialLot.getReserved6());
        requestTxItem.setZ_BATCH_REMARK(materialLot.getIncomingComment());
        requestTxItem.setZ_BATCH_DC(materialLot.getReserved9());
        requestTxItem.setZ_BATCH_MARING(materialLot.getLetteringInfo());
        requestTxItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
        requestTxItem.setZ_BATCH_MRB_CODE(materialLot.getReserved16());
        requestTxItem.setZ_BATCH_BONBOOK(materialLot.getReserved19());
        requestTxItem.setZ_BATCH_ABNO(materialLot.getReserved54());
        requestTxItem.setZ_BATCH_WMSBATCH(materialLot.getMaterialLotId());
        requestTxItem.setZ_BATCH_GFNO(materialLot.getIncomingDocId());
        requestTxItem.setZ_BATCH_INTERORDOR(materialLot.getReserved8());

        if (materialLot.getMaterialCategory().equals(Material.MATERIAL_CATEGORY_MAIN_MATERIAL)) {
            requestTxItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
        }else if (materialLot.getMaterialCategory().equals(Material.MATERIAL_CATEGORY_PRODUCT)){
            requestTxItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
        }
        requestTxItem.setZ_BATCH_CONTROLLOT(materialLot.getReserved4());
        requestTxItem.setZ_BATCH_CUSLOTNO(materialLot.getReserved5());
        requestTxItem.setZ_BATCH_CTNNO(materialLot.getReserved11());
        requestTxItem.setZ_BATCH_SO(materialLot.getReserved7());
        requestTxItem.setZ_BATCH_SOITEM(materialLot.getReserved15());
        requestTxItem.setZ_BATCH_CUSTUMER(materialLot.getReserved53());
        requestTxItem.setZ_BATCH_INCOMING(materialLot.getReserved17());
        requestTxItem.setZ_BATCH_PLANTRMA(materialLot.getReserved61());
        requestTxItem.setZ_BATCH_CUSRMA(materialLot.getReserved62());
        requestTxItem.setZ_BATCH_CUSSO(materialLot.getReserved25());
        requestTxItem.setZ_BATCH_MRB2(materialLot.getReserved52());
        requestTxItem.setZ_BATCH_VERSION2(materialLot.getReserved3());
        return requestTxItem;
    }
}
