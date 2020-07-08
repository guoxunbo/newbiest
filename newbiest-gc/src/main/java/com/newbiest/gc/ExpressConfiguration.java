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

    private String customerCode;

    private String platformFlag;

    private String appKey;

    private String appSecret;

}
