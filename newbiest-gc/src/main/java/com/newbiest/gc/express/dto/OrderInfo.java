package com.newbiest.gc.express.dto;

import com.newbiest.gc.ExpressConfiguration;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guoxunbo
 * @date 2020-07-08 16:03
 */
@Data
public class OrderInfo implements Serializable {


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
     * 付款卡号
     */
    private String paymentCustomer;

    /**
     * 物品类型
     */
    private String goodsType = ExpressConfiguration.DEFAULT_GOODS_TYPE;

    /**
     * ERP订单号
     */
    private String orderId;

    /**
     * 有无回单
     */
    private Integer receiptFlag = ExpressConfiguration.RECEIPT_FLAG_10;

    /**
     * 回单份数
     */
    private Integer receiptCount = ExpressConfiguration.DEFAULT_RECEIPT_COUNT;
}
