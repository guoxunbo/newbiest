package com.newbiest.gc.trigger;

import com.newbiest.common.trigger.TriggerContext;
import com.newbiest.gc.service.GcService;
import lombok.Data;

/**
 * Created by guoxunbo on 2019-11-12 15:34
 */
@Data
public class GcTriggerContext extends TriggerContext {

    private GcService gcService;

}
