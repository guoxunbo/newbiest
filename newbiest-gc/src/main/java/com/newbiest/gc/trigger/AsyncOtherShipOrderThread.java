package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangLuo on 2020-06-17
 */
public class AsyncOtherShipOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/5 * * * ?";

    public AsyncOtherShipOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncOtherStockOutOrder();
    }
}

