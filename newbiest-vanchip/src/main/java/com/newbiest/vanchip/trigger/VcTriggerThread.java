package com.newbiest.vanchip.trigger;

import com.newbiest.base.constant.EnvConstant;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.vanchip.service.VanChipService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public abstract class VcTriggerThread implements Runnable {
    protected VanChipService vanChipService;

    private static final Long ORG_RRN = 1L;

    @Override
    public void run() {
        generatorSessionContext();
        execute();
    }

    public abstract void execute();

    public void generatorSessionContext() {
        ThreadLocalContext.putOrgRrn(EnvConstant.GLOBAL_ORG_RRN);
        ThreadLocalContext.putUsername(StringUtils.SYSTEM_USER);
        ThreadLocalContext.putTransactionId(UUID.randomUUID().toString());
    }
}
