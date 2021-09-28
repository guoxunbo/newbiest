package com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingStockInRequestItem implements Serializable {

    /**
     * 物料凭证项目
     */
    private String ZEILE;

    /**
     * 物料编码
     */
    private String MATNR;

    /**
     * 存储地点
     */
    private String LGORT;

    /**
     * 数量
     */
    private BigDecimal MENGE;

    /**
     * 基本计量单位
     */
    private String MEINS;

    /**
     * 备注
     */
    private String SGTXT;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private IncomingStockInRequestTxItem TXItem;

    public IncomingStockInRequestItem copyMaterialLotToRequestItem(MaterialLot materialLot, IncomingStockInRequestItem requestItem){
        requestItem.setMATNR(materialLot.getMaterialName());
        requestItem.setLGORT(materialLot.getLastWarehouseId());
        requestItem.setMENGE(materialLot.getCurrentQty());
        requestItem.setMEINS(materialLot.getStoreUom());
        return requestItem;
    }
}
