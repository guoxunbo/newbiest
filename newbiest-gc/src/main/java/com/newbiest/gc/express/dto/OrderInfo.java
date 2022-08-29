package com.newbiest.gc.express.dto;

import com.newbiest.gc.ExpressConfiguration;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author guoxunbo
 * @date 2020-07-08 16:03
 */
@Data
public class OrderInfo implements Serializable {


    public static final String ORDER_STATUS_UN_DISPATCH = "未调度";

    public static final String RECEIVE_PAY_MODE = "20";
    public static final String SUB_SCRIPTION_SERVICE = "10";

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
     * 路由订阅服务
     */
    private String subscriptionService;

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

    /**
     * 是否取货
     */
    private Integer dismantling = ExpressConfiguration.DEFAULT_DISMANTLING;


    private String goodsTime = getDefaultGoodTime();

    /**
     * 下单公司
     */
    private String orderCompany;

    /**
     * 快递单状态
     */
    private String orderStatus;

    /**
     * 下单时间
     */
    private String orderTime = getDefaultGoodTime();

    /**
     * ERP单号
     */
    private String orderNumber;

    private String getDefaultGoodTime() {
        LocalDateTime ldt = LocalDateTime.now();
        ldt = ldt.withHour(19);
        ldt = ldt.withMinute(30);
        return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
