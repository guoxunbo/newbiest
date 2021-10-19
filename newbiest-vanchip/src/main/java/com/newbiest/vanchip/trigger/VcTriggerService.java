package com.newbiest.vanchip.trigger;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.common.trigger.NewbiestScheduleConfig;
import com.newbiest.common.trigger.service.TriggerService;
import com.newbiest.security.service.SecurityService;
import com.newbiest.vanchip.dto.trigger.TriggerRequest;
import com.newbiest.vanchip.dto.trigger.TriggerRequestHeader;
import com.newbiest.vanchip.service.ErpService;
import com.newbiest.vanchip.service.VanChipService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VcTriggerService implements InitializingBean {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    ErpService erpService;

    @Autowired
    TriggerService triggerService;

    @Autowired
    BaseService baseService;

    @Autowired
    SecurityService securityService;

    @Autowired
    NewbiestScheduleConfig newbiestScheduleConfig;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

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
            VcTriggerContext vcTriggerContext = new VcTriggerContext();
            vcTriggerContext.setVanChipService(vanChipService);
            vcTriggerContext.setTriggerService(triggerService);
            vcTriggerContext.setBaseService(baseService);
            vcTriggerContext.setSecurityService(securityService);
            vcTriggerContext.setErpService(erpService);
            generatorSessionContext();

            vcTriggerContext.setTriggerInstance(triggerService.getTriggerInstanceByName(AsyncDeliveryOrderThread.TRIGGER_NAME));
            AsyncDeliveryOrderThread asyncDeliveryOrderThread = new AsyncDeliveryOrderThread(vcTriggerContext);

            newbiestScheduleConfig.addTrigger(asyncDeliveryOrderThread);
        }).start();
    }

    public void generatorSessionContext(){
        try {
            ThreadLocalContext threadLocalContext = new ThreadLocalContext();
            TriggerRequest triggerRequest = new TriggerRequest();
            triggerRequest.setHeader(new TriggerRequestHeader(TriggerRequestHeader.DEFAULT_MESSAGE_NAME));

            String requestString = DefaultParser.getObjectMapper().writeValueAsString(triggerRequest);
            threadLocalContext.putRequest(requestString,"","", Maps.newHashMap());
        }catch (Exception e){
            throw ExceptionManager.handleException(e);
        }
    }


}
