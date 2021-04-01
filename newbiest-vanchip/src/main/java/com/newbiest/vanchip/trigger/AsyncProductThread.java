package com.newbiest.vanchip.trigger;

import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncProductThread extends VcTriggerThread {

    public static final String CRON_EXPRESS = "0 0/10 * * * ?";

    public AsyncProductThread(VanChipService vanChipService) {
        super(vanChipService);
    }

    @Override
    public void execute() {
        this.vanChipService.asyncMesProduct();
    }
}
