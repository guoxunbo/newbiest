package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangluo on 2020-12-02
 */
public class AsyncErpCogReceiveOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/10 * * * ?";

    public AsyncErpCogReceiveOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncCogReceiveOrder();
    }
}


