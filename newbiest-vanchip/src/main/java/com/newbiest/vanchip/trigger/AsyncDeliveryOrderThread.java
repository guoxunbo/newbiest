package com.newbiest.vanchip.trigger;

import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;

/**
 * 交货信息同步
 * 1.发货单
 * 2.RMA自身原因来料单
 */
@Slf4j
public class AsyncDeliveryOrderThread extends VcTriggerThread {
    //每10分钟触发
    public static final String CRON_EXPRESS = "0 0/10 * * * ?";

    //public static final String TRIGGER_NAME = "AsyncDeliveryOrderThread";

    public static final String message = "AsyncDeliveryOrder";

    public AsyncDeliveryOrderThread(VanChipService vanChipService) {
        super(vanChipService, message);
    }


    @Override
    public void execute() {
        this.vanChipService.asyncDeliveryInfo();
    }

//    public AsyncDeliveryOrderThread(TriggerContext triggerContext) {
//        super(triggerContext);
//    }
//
//    @Override
//    public TriggerResult execute() {
//        TriggerResult triggerResult = new TriggerResult();
//        try {
//            triggerResult.setResultCode(TriggerResult.RESULT_CODE_SUCCESS);
//            VcTriggerContext vcTriggerContext= (VcTriggerContext)triggerContext;
//            vcTriggerContext.getVanChipService().asyncDeliveryInfo();
//            return triggerResult;
//        }catch (Exception e){
//            log.error(e.getMessage(), e);
//            triggerResult.setResultCode(TriggerResult.RESULT_CODE_FAIL);
//            triggerResult.setResultText(e.getMessage());
//            return triggerResult;
//        }
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