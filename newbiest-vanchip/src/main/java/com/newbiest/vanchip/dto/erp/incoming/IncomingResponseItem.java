package com.newbiest.vanchip.dto.erp.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingResponseItem implements Serializable {

    @ApiModelProperty("交货单号")
    private String VBELN;

    @ApiModelProperty("项目号")
    private String POSNR;

    @ApiModelProperty("物料代码")
    private String MATNR;

    @ApiModelProperty("批次管理标识")
    private String XCHPF;

    @ApiModelProperty("实际交换量")
    private BigDecimal LGMNG;

    @ApiModelProperty("基本计量单位")
    private String MEINS;

    @ApiModelProperty("交货数量")
    private String LFIMG;

    @ApiModelProperty("采购订单单位")
    private String VRKME;

    @ApiModelProperty("物料描述")
    private String ARKTX;

    @ApiModelProperty("采购订单号")
    private String VGBEL;

    @ApiModelProperty("采购订单行项目号")
    private String VGPOS;

    @ApiModelProperty("供应商批次")
    private String LICHN;

    @ApiModelProperty("生产日期")
    private String HSDAT;

    @ApiModelProperty("总货架寿命")
    private String MHDHB;

    @ApiModelProperty("物料组")
    private String MATKL;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    public MaterialLot copyIncomingItemToMaterialLot(MaterialLot materialLot, IncomingResponseItem item){
        materialLot.setIncomingDocId(item.getVBELN());
        materialLot.setItemId(item.getPOSNR());
        materialLot.setMaterialName(item.getMATNR());
        materialLot.setCurrentQty(item.getLGMNG());
        materialLot.setIncomingQty(item.getLGMNG());
        materialLot.setReserved49(item.getLFIMG());
        materialLot.setReserved50(item.getVRKME());

        materialLot.setReserved60(item.getVGBEL());
        materialLot.setReserved59(item.getVGPOS());

        materialLot.setMaterialLotId(item.getLICHN());

        //生产日期
        return materialLot;
    }
}
