package com.newbiest.vanchip.dto.erp.backhaul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingOrReturnRequestItem implements Serializable {

    /**
     * 行项目号
     */
    private String ZEILE;

    /**
     * 物料编码
     */
    private String MATNR;

    /**
     * 工厂-无
     */
    private String WERKS;

    /**
     * 存储地点 仓库名
     */
    private String LGORT;

    /**
     * 数量
     */
    private BigDecimal MENGE;

    /**
     * 库存单位
     */
    private String MEINS;

    /**
     * 采购订单号
     */
    private String EBELN;

    /**
     * 采购订单号 行项目
     */
    private String EBELP;

    /**
     * 来料/退料 单据号
     */
    private String VBELN_IM;

    /**
     * 交货行项目号
     */
    private String POSNR;

    /**
     * 批次号
     */
    private String CHARG;

    /**
     * 备注
     */
    private String SGTXT;
    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;
    private IncomingOrReturnRequestTXItem TXItem;

    public IncomingOrReturnRequestItem copyMaterialLotToRequestItem(IncomingOrReturnRequestItem requestItem, MaterialLot materialLot){
        requestItem.setMATNR(materialLot.getMaterialName());
        requestItem.setLGORT(materialLot.getLastWarehouseId());
        requestItem.setMENGE(materialLot.getCurrentQty());
        requestItem.setMEINS(materialLot.getStoreUom());
        requestItem.setVBELN_IM(materialLot.getIncomingDocId());
        requestItem.setSGTXT(materialLot.getIncomingComment());
        requestItem.setEBELP(materialLot.getReserved59());
        requestItem.setEBELN(materialLot.getReserved20());
        return requestItem;
    }
}
