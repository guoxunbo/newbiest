package com.newbiest.vanchip.trigger;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.common.trigger.TriggerContext;
import com.newbiest.common.trigger.model.TriggerResult;
import com.newbiest.common.trigger.thread.TriggerThead;
import com.newbiest.vanchip.dto.trigger.TriggerRequest;
import com.newbiest.vanchip.dto.trigger.TriggerRequestHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * 交货信息同步
 * 1.发货单
 * 2.RMA自身原因来料单
 */
@Slf4j
public class AsyncDeliveryOrderThread extends TriggerThead {
    //每1分钟触发
    public static final String CRON_EXPRESS = "0 0/1 * * * ?";

    public static final String TRIGGER_NAME = "AsyncDeliveryOrderThread";

    public AsyncDeliveryOrderThread(TriggerContext triggerContext) {
        super(triggerContext);
    }

    @Override
    public TriggerResult execute() {
        TriggerResult triggerResult = new TriggerResult();
        try {
            triggerResult.setResultCode(TriggerResult.RESULT_CODE_SUCCESS);
            VcTriggerContext vcTriggerContext= (VcTriggerContext)triggerContext;
            vcTriggerContext.getVanChipService().asyncDeliveryInfo();
            return triggerResult;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            triggerResult.setResultCode(TriggerResult.RESULT_CODE_FAIL);
            triggerResult.setResultText(e.getMessage());
            return triggerResult;
        }
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