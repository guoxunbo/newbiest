package com.newbiest.vanchip.dto.erp.backhaul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingOrReturnRequestTXItem implements Serializable {

    /**
     * PO 号
     */
    private String Z_BATCH_PONUMBER;

    /**
     * 供应商代码
     */
    private String Z_BATCH_VENDOR;

    /**
     * remark
     */
    private String Z_BATCH_REMARK;

    /**
     * 内盒号
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
     * bin别
     */
    private String Z_BATCH_BINTYPE;

    /**
     * 客户订单号码
     */
    private String Z_BATCH_CUSPO;

    /**
     * 销售订单号
     */
    private String Z_BATCH_SO;

    /**
     * 销售订单项目号
     */
    private String Z_BATCH_SOITEM;

    /**
     * BOX NO
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
     * MRB-CODE
     */
    private String Z_BATCH_MRB_CODE;

    /**
     * 测试批次
     */
    private String Z_BATCH_TBATCH;

    /**
     * 大类描述
     */
    private String Z_BATCH_TYPEDES;

    /**
     * 供应商批次
     */
    private String Z_BATCH_VBATCH;

    /**
     * 供应商名称
     */
    private String Z_BATCH_VNAME;

    /**
     * 供应商地址
     */
    private String Z_BATCH_VADDRESS;


    public IncomingOrReturnRequestTXItem copyMaterialLotToRequestTXItem(IncomingOrReturnRequestTXItem requestTXItem, MaterialLot materialLot){
        requestTXItem.setZ_BATCH_PONUMBER(materialLot.getReserved20());
        requestTXItem.setZ_BATCH_VENDOR(materialLot.getReserved57());
        requestTXItem.setZ_BATCH_REMARK(materialLot.getIncomingComment());
        requestTXItem.setZ_BATCH_DC(materialLot.getReserved9());
        requestTXItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
        requestTXItem.setZ_BATCH_CUSPO(materialLot.getReserved54());
        requestTXItem.setZ_BATCH_CONTROLLOT(materialLot.getReserved4());
        requestTXItem.setZ_BATCH_CUSLOTNO(materialLot.getReserved5());
        requestTXItem.setZ_BATCH_MRB_CODE(materialLot.getReserved16());
        requestTXItem.setZ_BATCH_MARING(materialLot.getLetteringInfo());
        requestTXItem.setZ_BATCH_VBATCH(materialLot.getMaterialLotId());
        requestTXItem.setZ_BATCH_VNAME(materialLot.getReserved48());
        //大类描述 外面赋值

        //requestTXItem.setZ_BATCH_SOITEM("销售订单号");
        //销售订单号项目号

        //BOX NO 无需处理
        //Z_BATCH_REEL
        //测试批次
        //供应商地址
        return requestTXItem;
    }
}
