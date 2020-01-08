package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by guoxunbo on 2019-11-15 14:19
 */
public class AsyncMaterialOutOrderThread extends GcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/5 * * * ?";

    public AsyncMaterialOutOrderThread(GcService gcService) {
        super(gcService);
    }

    @Override
    public void execute() {
        this.gcService.asyncErpMaterialOutOrder();
    }
}

