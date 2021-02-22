package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangLuo on 2021-01-11
 */
public class AsyncMaterialIssueOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/5 * * * ?";

    public AsyncMaterialIssueOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncMaterialIssueOrder();
    }
}

