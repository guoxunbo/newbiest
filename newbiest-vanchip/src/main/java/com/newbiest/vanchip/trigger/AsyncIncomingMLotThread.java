package com.newbiest.vanchip.trigger;

import com.newbiest.vanchip.service.VanChipService;

/**
 * 采购到货接口同步
 * 1.辅材来料单
 * 2.辅材退料单
 */
public class AsyncIncomingMLotThread extends VcTriggerThread {
    //每10分钟触发
    public static final String CRON_EXPRESS = "0 0/10 * * * ?";

    public static final String message = "AsyncIncomingMLot";

    public AsyncIncomingMLotThread(VanChipService vanChipService) {
        super(vanChipService, message);
    }


    @Override
    public void execute() {
        this.vanChipService.asyncIncomingOrReturn();
    }
}