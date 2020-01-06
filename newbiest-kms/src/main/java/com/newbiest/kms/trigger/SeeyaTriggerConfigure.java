package com.newbiest.kms.trigger;

import com.newbiest.kms.service.SeeyaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * GC定时服务。因为GC1期版本使用的是1.0.3.故此处不考虑同步数据多次同步的问题。即不考虑分布式锁
 */
@EnableScheduling
@Configuration
public class SeeyaTriggerConfigure implements SchedulingConfigurer {

    @Autowired
    SeeyaService seeyaService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
        scheduledTaskRegistrar.addCronTask(new AsyncMesUserThread(seeyaService), AsyncMesUserThread.CRON_EXPRESS);
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
