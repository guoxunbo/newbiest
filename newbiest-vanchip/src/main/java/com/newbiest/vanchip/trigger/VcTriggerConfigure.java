package com.newbiest.vanchip.trigger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
@ConditionalOnProperty(name="vc.openSchedule", havingValue = "true")
public class VcTriggerConfigure {

}
