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

    //ship Channels
    private String shipChannels;

    //发货产品类型
    private String shipPartNumber;

    //物料编码
    private String materialCode;

    //预约送货日期
    private String expectationDeliveryDate;

    //pk类型
    private String pkType;

    //创建人
    private String createdBy;

    //客户代码
    private String customerCode;

    //终端客户
    private String terminalCustomer;

    private List<DeliveryMLotPrintInfo> deliveryMLotPrintInfoList;
}
