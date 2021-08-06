package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShippingListPrintInfo implements Serializable {

    private String deliveryOrderLineId;

    private String shipAdd;

    private String shipTo;

    private String shippingDate;

    private String contact;

    private String tel;

    //总箱数
    private String totalBoxQty;

    //总数量
    private String totalQty;

    //承运人
    private String freighter;

    //物流信息
    private String logisticsInfo;


    private List<ShippingListBoxPrintInfo> shipListBoxPrintInfos;
}
