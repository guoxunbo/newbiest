package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ScmService;

/**
 * 对失败的接口做重试
 */
public class RetryInterfaceThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 */5 * * * ?";

    public RetryInterfaceThread(GcService gcService, ScmService scmService) {
        super(gcService, scmService);
    }

    @Override
    public void execute() {
        this.scmSerice.retry();
    }
}