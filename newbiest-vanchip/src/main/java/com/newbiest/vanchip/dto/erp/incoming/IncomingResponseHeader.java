package com.newbiest.vanchip.dto.erp.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingResponseHeader implements Serializable {

    @ApiModelProperty("来料")
    public final static String INCOMING_LFART = "EL";

    @ApiModelProperty("退料")
    public final static String RETURN_LFART = "RL";
    @ApiModelProperty("交货状态")
    public final static String SDABW_RED = "RED";


    @ApiModelProperty("交货单")
    private String VBELN;

    @ApiModelProperty("创建人")
    private String ERNAM;

    @ApiModelProperty("交货状态")
    private String SDABW;

    @ApiModelProperty(value = "创建日期",example = "20210701")
    private String ERDAT;

    @ApiModelProperty(value = "时间")
    private String ERZET;

    @ApiModelProperty(value = "交货类型")
    private String LFART;

    @ApiModelProperty("供应商编码")
    private String LIFNR;

    @ApiModelProperty("供应商名称")
    private String NAME1;

    @ApiModelProperty("供应商地址")
    private String STRAS;

    @ApiModelProperty("REMARK")
    private String REMARK;

    @ApiModelProperty("取件人")
    private String ZPERSON;

    @ApiModelProperty("承运公司")
    private String ZCOMPANY;

    @ApiModelProperty("运单号码")
    private String ZDNUMBER;

    @ApiModelProperty("预计送达时间")
    private String LFDAT;

    @ApiModelProperty("件数")
    private String ZPICES;

    @ApiModelProperty("Carton Size(mm)")
    private String ZCARTON;

    @ApiModelProperty("Qty Of Carton")
    private String ZQCARTON;

    @ApiModelProperty("G.W.(kgs)")
    private String ZGW;

    @ApiModelProperty("N.W.(kgs)")
    private String ZNW;

    @ApiModelProperty("贸易类型")
    private String ZTYPE;

    @ApiModelProperty("商品编码")
    private String ZHS;

    @ApiModelProperty("ECCN")
    private String ZECCN;

    @ApiModelProperty("保税手册")
    private String ZTAX;

    @ApiModelProperty("关税RMB")
    private String ZTAX2;

    @ApiModelProperty("增值税")
    private String ZTAXRMB;

    @ApiModelProperty("进口关单号码")
    private String ZCUSTOMS;

    @ApiModelProperty("物料运费RMB")
    private String ZLOGISTIC;

    @ApiModelProperty("保险费RMB")
    private String ZINSURANCE;

    @ApiModelProperty("其他费用RMB")
    private String ZOTHER;

    @ApiModelProperty("费用合计")
    private String ZTOTAL;

    @ApiModelProperty("贸易国别")
    private String ZTRADE;

    @ApiModelProperty("发票号码")
    private String ZINVOICE;

    @ApiModelProperty("产地")
    private String ZORIGIN;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;



    public MaterialLot copyIncomingHeaderToMaterialLot(MaterialLot materialLot, IncomingResponseHeader header){
        materialLot.setIncomingDocId(header.getVBELN());
        materialLot.setReserved48(header.getNAME1());
        materialLot.setReserved57(header.getLIFNR());
        materialLot.setIncomingComment(header.getREMARK());
        materialLot.setReserved22(header.getZPERSON());
        materialLot.setReserved23(header.getZCOMPANY());
        materialLot.setReserved24(header.getZDNUMBER());
        materialLot.setReserved26(header.getZPICES());
        materialLot.setReserved38(header.getZINVOICE());
        materialLot.setReserved10(header.getZCARTON());
        materialLot.setReserved12(header.getZNW());
        materialLot.setReserved13(header.getZGW());
        materialLot.setReserved27(header.getZHS());
        materialLot.setReserved28(header.getZECCN());
        materialLot.setReserved19(header.getZTAX());
        materialLot.setReserved30(header.getZTAX2());
        materialLot.setReserved31(header.getZTAXRMB());
        materialLot.setReserved32(header.getZCUSTOMS());
        materialLot.setReserved33(header.getZLOGISTIC());
        materialLot.setReserved34(header.getZINSURANCE());
        materialLot.setReserved35(header.getZOTHER());
        materialLot.setReserved36(header.getZTOTAL());
        materialLot.setReserved37(header.getZTRADE());
        materialLot.setReserved51(header.getZORIGIN());
        return materialLot;
    }
}
