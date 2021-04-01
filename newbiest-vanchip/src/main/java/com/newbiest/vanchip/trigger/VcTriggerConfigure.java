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
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
        scheduledTaskRegistrar.addCronTask(new AsyncProductThread(vanChipService), AsyncProductThread.CRON_EXPRESS);
    }

    /**
     * 创建trigger的线程池，线程池大小为triggerInst长度+Daemon线程
     * @return
     */
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return new ScheduledThreadPoolExecutor(5);
    }
}
