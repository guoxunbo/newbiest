package com.newbiest.vanchip.dto.erp.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.Maps;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingResponseItem implements Serializable {

    @ApiModelProperty("交货单号")
    private String VBELN;

    @ApiModelProperty("项目号")
    private String POSNR;

    @ApiModelProperty("物料代码")
    private String MATNR;

    @ApiModelProperty("库位")
    private String LGORT;

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
    private String LICHN;//字段不同步

    @ApiModelProperty("生产日期")
    private String HSDAT;//字段不同步

    @ApiModelProperty("总货架寿命")
    private String MHDHB;

    @ApiModelProperty("物料组")
    private String MATKL;

    @ApiModelProperty("申请人")
    private String ZPERSON;

    @ApiModelProperty("联系人")
    private String CONTACT;

    @ApiModelProperty("联系人电话")
    private String TELNUMBER;

    @ApiModelProperty("退货RMA号")
    private String ZRETURNMRA;

    @ApiModelProperty("国贸条款")
    private String INCO1;

    @ApiModelProperty("物料信息")
    private String INCO2_L;

    @ApiModelProperty("REMARK")
    private String REMARK;

    @ApiModelProperty("原采购订单")
    private String REF_DOC_NR;

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
        materialLot.setReserved20(item.getVGBEL());
        materialLot.setReserved59(item.getVGPOS());
        return materialLot;
    }

    public Map<String, Object> convertMLotMap(){
        Map<String, Object> map = Maps.newHashMap();
        map.put("incomingDocId", VBELN);
        map.put("itemId", POSNR);
        map.put("materialName", MATNR);
        map.put("currentQty", LGMNG);
        map.put("incomingQty", LGMNG);
        map.put("reserved49", LFIMG);
        map.put("reserved50", VRKME);
        map.put("reserved20", VGBEL);
        map.put("reserved59", VGPOS);
        return map;
    }
}
