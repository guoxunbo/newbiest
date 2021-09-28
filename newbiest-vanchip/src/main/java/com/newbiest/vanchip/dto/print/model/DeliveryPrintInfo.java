package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发货单
 */
@Data
public class DeliveryPrintInfo implements Serializable {

    private String deliveryOrderLineId;

    private String shipFrom;

    private String shipTo;

    private String shipAdd;

    private String contact;

    private String tel;

    private String shippingDate;
    private String customerPO;
    private String invNo;
    private String soNo;

    //总箱数
    private String totalBoxQty;

    //总数
    private String totalQty;

    //承运人
    private String freighter;

    //物流信息
    private String logisticsInfo;

    private List<DeliveryMLotPrintInfo> deliveryMLotPrintInfoList;
}
