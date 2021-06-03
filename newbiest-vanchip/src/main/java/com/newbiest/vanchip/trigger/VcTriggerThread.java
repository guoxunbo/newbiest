package com.newbiest.vanchip.trigger;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.vanchip.dto.trigger.TriggerRequest;
import com.newbiest.vanchip.dto.trigger.TriggerRequestHeader;
import com.newbiest.vanchip.service.VanChipService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public abstract class VcTriggerThread implements Runnable{

    protected VanChipService vanChipService;

    private static final Long ORG_RRN = 1L;

    @Override
    public void run() {
        generatorThreadLocalContext();
        execute();
    }

    public abstract void execute();

    public void generatorThreadLocalContext(){
        try {
            ThreadLocalContext threadLocalContext = new ThreadLocalContext();
            TriggerRequest triggerRequest = new TriggerRequest();
            triggerRequest.setHeader(new TriggerRequestHeader());
            String requestString = DefaultParser.getObjectMapper().writeValueAsString(triggerRequest);
            threadLocalContext.putRequest(requestString,"","", Maps.newHashMap());
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}
