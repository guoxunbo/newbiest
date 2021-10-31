package com.newbiest.vanchip.trigger;

import org.springframework.stereotype.Component;

@Component
public class VcTriggerService{

//    @Autowired
//    VanChipService vanChipService;
//
//    @Autowired
//    ErpService erpService;
//
//    @Autowired
//    TriggerService triggerService;
//
//    @Autowired
//    BaseService baseService;
//
//    @Autowired
//    SecurityService securityService;
//
//    @Autowired
//    NewbiestScheduleConfig newbiestScheduleConfig;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//
//    }
//
//    @PostConstruct
//    public void init() {
//        generatorSessionContext();
//        new Thread(() -> {
//            while (!NewbiestScheduleConfig.getIsInit().get()) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//
//                }
//            }
//            VcTriggerContext vcTriggerContext = new VcTriggerContext();
//            vcTriggerContext.setVanChipService(vanChipService);
//            vcTriggerContext.setTriggerService(triggerService);
//            vcTriggerContext.setBaseService(baseService);
//            vcTriggerContext.setSecurityService(securityService);
//            vcTriggerContext.setErpService(erpService);
//            generatorSessionContext();
//
//            vcTriggerContext.setTriggerInstance(triggerService.getTriggerInstanceByName(AsyncDeliveryOrderThread.TRIGGER_NAME));
//            AsyncDeliveryOrderThread asyncDeliveryOrderThread = new AsyncDeliveryOrderThread(vcTriggerContext);
//
//            newbiestScheduleConfig.addTrigger(asyncDeliveryOrderThread);
//        }).start();
//    }
//
//    public void generatorSessionContext(){
//        try {
//            ThreadLocalContext threadLocalContext = new ThreadLocalContext();
//            TriggerRequest triggerRequest = new TriggerRequest();
//            triggerRequest.setHeader(new TriggerRequestHeader(TriggerRequestHeader.DEFAULT_MESSAGE_NAME));
//
//            String requestString = DefaultParser.getObjectMapper().writeValueAsString(triggerRequest);
//            threadLocalContext.putRequest(requestString,"","", Maps.newHashMap());
//        }catch (Exception e){
//            throw ExceptionManager.handleException(e);
//        }
//    }


}
