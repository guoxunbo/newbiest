package com.newbiest.gc.trigger;

import com.newbiest.base.constant.EnvConstant;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.common.trigger.NewbiestScheduleConfig;
import com.newbiest.common.trigger.service.TriggerService;
import com.newbiest.gc.service.GcService;
import com.newbiest.security.model.NBOrg;
import com.newbiest.security.service.SecurityService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

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
        ThreadLocalContext.putOrgRrn(EnvConstant.GLOBAL_ORG_RRN);
        ThreadLocalContext.putUsername(StringUtils.SYSTEM_USER);
        ThreadLocalContext.putTransactionId(UUID.randomUUID().toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
