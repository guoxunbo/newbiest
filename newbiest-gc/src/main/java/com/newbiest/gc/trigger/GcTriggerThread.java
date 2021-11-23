package com.newbiest.gc.trigger;

import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ScmService;
import com.newbiest.security.model.NBOrg;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * Created by guoxunbo on 2019-11-15 14:32
 */
@Data
public abstract class GcTriggerThread implements Runnable{

    protected GcService gcService;
    protected ScmService scmSerice;

    private static final Long ORG_RRN = 1L;

    public GcTriggerThread(GcService gcService){
        this.gcService = gcService;
    }

    public GcTriggerThread(GcService gcService, ScmService scmService) {
        this.gcService = gcService;
        this.scmSerice = scmService;
    }

    @Override
    public void run() {
        generatorSessionContext();
        execute();
    }

    public abstract void execute();

    public void generatorSessionContext() {
        SessionContext sc = new SessionContext();
        sc.setOrgRrn(this.ORG_RRN);
        sc.setUsername(StringUtils.SYSTEM_USER);
        sc.setTransRrn(UUID.randomUUID().toString());
        sc.setTransTime(new Date());
        ThreadLocalContext.putSessionContext(sc);
    }
}
