package com.newbiest.vanchip.trigger;

import com.newbiest.vanchip.service.VanChipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@EnableScheduling
@Configuration
@ConditionalOnProperty(name="vc.openSchedule", havingValue = "true")
public class VcTriggerConfigure implements SchedulingConfigurer {

    @Autowired
    VanChipService vanChipService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addCronTask(new AsyncPreWaringThread(vanChipService), AsyncPreWaringThread.CRON_EXPRESS);
        taskRegistrar.addCronTask(new AsyncIncomingMLotThread(vanChipService), AsyncIncomingMLotThread.CRON_EXPRESS);
        taskRegistrar.addCronTask(new AsyncDeliveryOrderThread(vanChipService), AsyncDeliveryOrderThread.CRON_EXPRESS);

    }

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return new ScheduledThreadPoolExecutor(5);
    }
}
