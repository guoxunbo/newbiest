package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangLuo on 2020-06-17
 */
public class AsyncOtherIssueOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/5 * * * ?";

    public AsyncOtherIssueOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncOtherIssueOrder();
    }
}

