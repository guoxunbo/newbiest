package com.newbiest.gc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author guoxunbo
 * @date 2020-07-08 14:07
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "gc.express")
public class ExpressConfiguration {

    public static final String PLAN_ORDER_METHOD = "open.api.openCommon.planOrder";
    public static final String CANCEL_ORDER_METHOD = "open.api.openCommon.cancelOrder";
    public static final String QUERY_ROUTE_METHOD = "open.api.openCommon.queryRoute";
    public static final String QUERY_ORDER_STATUS_METHOD = "open.api.openCommon.queryCancelOrder";

    public static final String PLAN_ORDER_DEFAULT_ORDER_ID = "GC00000001";


    public static final String DEFAULT_GOODS_TYPE = "芯片/晶圆";

    /**
     * 回单原件（含电子回单图片
     */
    public static final Integer RECEIPT_FLAG_10 = 10;

    /**
     * 无（不需要签回单），
     */
    public static final Integer RECEIPT_FLAG_20 = 20;

    /**
     * 电子回单图片
     */
    public static final Integer RECEIPT_FLAG_30 = 30;

    /**
     * 默认的回单份数
     */
    public static final Integer DEFAULT_RECEIPT_COUNT = 1;

    /**
     * 默认的预约取货
     */
    public static final Integer DEFAULT_DISMANTLING = 10;

    private String customerCode;

    private String zjCustomerCode;

    private String platformFlag;

    private String appKey;

    private String appSecret;

}
