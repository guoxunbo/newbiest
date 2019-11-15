package com.newbiest.gc.trigger;

import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.trigger.NewbiestScheduleConfig;
import com.newbiest.common.trigger.service.TriggerService;
import com.newbiest.common.trigger.thread.DaemonThread;
import com.newbiest.gc.service.GcService;
import com.newbiest.security.model.NBOrg;
import com.newbiest.security.service.SecurityService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by guoxunbo on 2019-11-12 15:39
 */
@Component
public class GCTriggerService implements InitializingBean {


    @Autowired
    GcService gcService;

    @Autowired
    TriggerService triggerService;

    @Autowired
    BaseService baseService;

    @Autowired
    SecurityService securityService;

    @Autowired
    NewbiestScheduleConfig newbiestScheduleConfig;

    @PostConstruct
    public void init() {
        generatorSessionContext();
        new Thread(() -> {
            while (!NewbiestScheduleConfig.getIsInit().get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
            GcTriggerContext gcTriggerContext = new GcTriggerContext();
            gcTriggerContext.setGcService(gcService);
            gcTriggerContext.setTriggerService(triggerService);
            gcTriggerContext.setBaseService(baseService);
            gcTriggerContext.setSecurityService(securityService);
            generatorSessionContext();
            gcTriggerContext.setTriggerInstance(triggerService.getTriggerInstanceByName(TestThread.TRIGGER_NAME));
            TestThread testThread = new TestThread(gcTriggerContext);
            newbiestScheduleConfig.addTrigger(testThread);
        }).start();
    }

    public void generatorSessionContext() {
        SessionContext sc = new SessionContext();
        sc.setOrgRrn(NBOrg.GLOBAL_ORG_RRN);
        sc.setUsername(StringUtils.SYSTEM_USER);
        sc.setTransRrn(UUID.randomUUID().toString());
        sc.setTransTime(new Date());
        ThreadLocalContext.putSessionContext(sc);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("a");
    }
}
