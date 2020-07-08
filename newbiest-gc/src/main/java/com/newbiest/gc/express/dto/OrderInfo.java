package com.newbiest.gc.express.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoxunbo
 * @date 2020-07-08 16:03
 */
@Data
public class OrderInfo implements Serializable {

    public static final String DEFAULT_GOODS_TYPE = "WAFER";

    /**
     * 快递单号 为空的话由跨越速递自动生成
     */
    private String waybillNumber;

    /**
     * 寄件人信息
     */
    private WaybillDelivery preWaybillDelivery;

    /**
     * 收件人信息
     */
    private WaybillDelivery preWaybillPickup;

    /**
     * 服务方式
     */
    private Integer serviceMode;

    /**
     * 付款方式
     */
    private Integer payMode;

    /**
     * 物品类型
     */
    private String goodsType = DEFAULT_GOODS_TYPE;

    /**
     * ERP订单号
     */
    private String orderId;

    /**
     * 有无回单
     */
    private Integer receiptFlag = 20;

}
