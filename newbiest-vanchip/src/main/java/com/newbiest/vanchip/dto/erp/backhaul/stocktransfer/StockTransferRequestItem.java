package com.newbiest.vanchip.dto.erp.backhaul.stocktransfer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class StockTransferRequestItem implements Serializable {


    //项目号
    private String ZEILE;
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

    public StockTransferRequestItem copyMaterialLotActionToStockTransferRequestItem(StockTransferRequestItem requestItem, MaterialLotAction materialLotAction){
        requestItem.setUMLGO(materialLotAction.getTargetWarehouseId());
        requestItem.setLGORT(materialLotAction.getFromWarehouseId());
        requestItem.setERFMG(materialLotAction.getTransQty());
        return requestItem;
    }
}
