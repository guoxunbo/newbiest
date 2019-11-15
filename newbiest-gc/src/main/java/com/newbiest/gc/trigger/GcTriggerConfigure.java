package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * GC定时服务。因为GC1期版本使用的是1.0.4.故此处不考虑同步数据多次同步的问题。即不考虑分布式锁
 * 通过使用gc.openSchedule来保证只有一台启用了定时服务
 * Created by guoxunbo on 2019-11-15 14:17
 */
@EnableScheduling
@Configuration
@ConditionalOnProperty(name="gc.openSchedule", havingValue = "true")
public class GcTriggerConfigure implements SchedulingConfigurer {

    @Autowired
    GcService gcService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
        scheduledTaskRegistrar.addCronTask(new AsyncErpSoThread(gcService), AsyncErpSoThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncMaterialOutOrderThread(gcService), AsyncMaterialOutOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncProductThread(gcService), AsyncProductThread.CRON_EXPRESS);

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
