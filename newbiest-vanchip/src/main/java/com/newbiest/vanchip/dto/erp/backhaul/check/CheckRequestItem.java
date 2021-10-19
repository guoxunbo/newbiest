package com.newbiest.vanchip.dto.erp.backhaul.check;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class CheckRequestItem implements Serializable {

    /**
     * 仓库类型
     * 1为非限制，
     * 2为质量检验，
     * 4为已冻结，
     */
    public static final String BSTAR_1 = "1";
    public static final String BSTAR_2 = "2";
    public static final String BSTAR_4 = "4";

    /**
     * 项目
     */
    private String ZEILI;

    /**
     * 物料代码
     */
    private String MATNR;

    /**
     * 物料描述
     */
    private String MAKTX;


    /**
     * 库存类型
     */
    private String BSTAR;


    /**
     * 基本计量单位
     */
    private String ERFME;

    /**
     * 数量
     */
    private BigDecimal ERFMG;

    /**
     * 二次计数
     */
    private String ZCOUNT;

    /**
     * reel
     */
    private String Z_BATCH_REEL;

    /**
     * 测试批次
     */
    private String Z_BATCH_TBATCH;

    /**
     * BOX NO
     */
    private String Z_BATCH_BOXNO;

    /**
     * WMS 系统生成批次
     */
    private String Z_BATCH_WMSBATCH;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    public CheckRequestItem copyMaterialLotToCheckRequestItem(CheckRequestItem requestItem, MaterialLot materialLot){
        requestItem.setMATNR(materialLot.getMaterialName());
        requestItem.setMAKTX(materialLot.getMaterialDesc());
        requestItem.setERFME(materialLot.getStoreUom());
        requestItem.setERFMG(materialLot.getCurrentQty());
        requestItem.setZ_BATCH_WMSBATCH(materialLot.getMaterialLotId());
        requestItem.setBSTAR(MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState()) ? BSTAR_1 : BSTAR_4);
        return requestItem;
    }
}
