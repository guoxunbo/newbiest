package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangluo on 2020-09-07
 */
public class AsyncPoSupplierThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/15 * * * ?";

    public AsyncPoSupplierThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncPoSupplier();
    }
}