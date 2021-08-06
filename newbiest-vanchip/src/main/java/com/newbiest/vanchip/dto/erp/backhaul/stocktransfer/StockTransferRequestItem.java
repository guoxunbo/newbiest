package com.newbiest.vanchip.dto.erp.backhaul.stocktransfer;

import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StockTransferRequestItem implements Serializable {

    //项目号
    private Integer ZEILE;
    //物料号
    private String MATNR;
    //当前仓库
    private String LGORT;
    //数量
    private BigDecimal ERFMG;
    //单位
    private String MEINS;
    //文本
    private String SGTXT;
    //目标仓库
    private String UMLGO;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    //测试批次号
    private String Z_BATCH_TBATCH;
    //待测品boxNo
    private String Z_BATCH_BOXNO;
    //成品
    private String Z_BATCH_REEL;

    private String Z_BATCH_WMSBATCH;


    public StockTransferRequestItem copyMaterialLotToStockTransferRequestItem(StockTransferRequestItem requestItem, MaterialLot materialLot){
        requestItem.setMATNR(materialLot.getMaterialName());
        requestItem.setERFMG(materialLot.getCurrentQty());
        requestItem.setMEINS(materialLot.getStoreUom());
        requestItem.setZ_BATCH_WMSBATCH(materialLot.getMaterialLotId());
        return requestItem;
    }

}
