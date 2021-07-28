package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangluo on 2020-08-21
 */
public class AsyncProductRelationThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/20 * * * ?";

    public AsyncProductRelationThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncProductGradeAndSubcode();
    }
}