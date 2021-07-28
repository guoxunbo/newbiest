package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;

/**
 * Created by guozhangLuo on 2021-07-28
 */
public class AsyncRawMaterialOtherShipOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/5 * * * ?";

    public AsyncRawMaterialOtherShipOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncRawMaterialOtherShipOrder();
    }
}

