package com.newbiest.vanchip.trigger;

import com.newbiest.vanchip.service.VanChipService;

public class AsyncPreWaringThread extends VcTriggerThread {
    //0 0 1,7,13,19 * * ?
    public static final String CRON_EXPRESS = "0 0 1,7,13,19 * * ?";

    public static final String message = "PreWaringManager";

    public AsyncPreWaringThread(VanChipService vanChipService) {
        super(vanChipService, message);
    }

    @Override
    public void execute() {
        this.vanChipService.preWarning();
    }
}