package com.newbiest.vanchip.trigger;
import com.newbiest.common.trigger.TriggerContext;
import com.newbiest.vanchip.service.ErpService;
import com.newbiest.vanchip.service.VanChipService;
import lombok.Data;

@Data
public class VcTriggerContext extends TriggerContext {

    private VanChipService vanChipService;

    private ErpService erpService;
}
