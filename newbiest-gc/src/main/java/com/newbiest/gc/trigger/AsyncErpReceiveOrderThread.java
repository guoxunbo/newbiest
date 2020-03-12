package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guoxunbo on 2019-11-15 14:19
 */
public class AsyncErpReceiveOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/10 * * * ?";

    public AsyncErpReceiveOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncReceiveOrder();
    }
}


