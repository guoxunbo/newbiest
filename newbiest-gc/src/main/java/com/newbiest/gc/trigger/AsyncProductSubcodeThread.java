package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangluo on 2020-03-10
 */
public class AsyncProductSubcodeThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/60 * * * ?";

    public AsyncProductSubcodeThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncMesProductAndSubcode();
    }
}