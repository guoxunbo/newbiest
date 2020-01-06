package com.newbiest.kms.trigger;

import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.kms.service.SeeyaService;
import com.newbiest.security.model.NBOrg;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Created by guoxunbo on 2020-01-06 16:56
 */
@Data
@AllArgsConstructor
public class AsyncMesUserThread implements Runnable{

    public static final String CRON_EXPRESS = "0 0/30 * * * ?";

    protected SeeyaService seeyaService;

    @Override
    public void run() {
        generatorSessionContext();
        seeyaService.asyncMesUser();
    }

    public void generatorSessionContext() {
        SessionContext sc = new SessionContext();
        sc.setOrgRrn(NBOrg.GLOBAL_ORG_RRN);
        sc.setUsername("System");
        sc.setTransRrn(UUID.randomUUID().toString());
        sc.setTransTime(DateUtils.now());
        ThreadLocalContext.putSessionContext(sc);
    }
}
