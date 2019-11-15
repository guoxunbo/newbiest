package com.newbiest.gc.trigger;

import com.newbiest.common.trigger.TriggerContext;
import com.newbiest.common.trigger.model.TriggerResult;
import com.newbiest.common.trigger.thread.TriggerThead;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by guoxunbo on 2019-11-12 15:33
 */
@Slf4j
public class TestThread extends TriggerThead {

    public static final String TRIGGER_NAME = "GCTestTrigger";

    public TestThread(TriggerContext triggerContext) {
        super(triggerContext);
    }

    @Override
    public TriggerResult execute() {
        TriggerResult triggerResult = new TriggerResult();
        try {
            GcTriggerContext gcTriggerContext = (GcTriggerContext) triggerContext;
            gcTriggerContext.getGcService().getStockOutCheckList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            triggerResult.setResultCode(TriggerResult.RESULT_CODE_FAIL);
            triggerResult.setResultText(e.getMessage());
        }
        return triggerResult;

    }
}
