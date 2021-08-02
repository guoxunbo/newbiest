package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 发货单
 */
@Data
public class DeliveryPrintInfo implements Serializable {

    private String deliveryOrderLineId;

    private String shipFrom;

    private String shipTo;

    private String shippingDate;

    private String contact;

    private String tel;

    //总箱数
    private String totalBoxQty;

    //总数
    private String totalQty;

    //承运人
    private String freighter;

    //物流信息
    private String logisticsInfo;

}
