package com.newbiest.gc.trigger;

import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ScmService;
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

    @Autowired
    ScmService scmService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());
        scheduledTaskRegistrar.addCronTask(new AsyncErpReceiveOrderThread(gcService), AsyncErpReceiveOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncErpShipOrderThread(gcService), AsyncErpShipOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncReTestOrderThread(gcService), AsyncReTestOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncWaferIssueOrderThread(gcService), AsyncWaferIssueOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncOtherStockOutOrderThread(gcService), AsyncOtherStockOutOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncOtherShipOrderThread(gcService), AsyncOtherShipOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncErpCogReceiveOrderThread(gcService), AsyncErpCogReceiveOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncMaterialIssueOrderThread(gcService), AsyncMaterialIssueOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncRawMaterialOtherShipOrderThread(gcService), AsyncRawMaterialOtherShipOrderThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncFtRetestIssueOrderThread(gcService), AsyncFtRetestIssueOrderThread.CRON_EXPRESS);

        scheduledTaskRegistrar.addCronTask(new AsyncMaterialNameThread(gcService), AsyncMaterialNameThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncProductThread(gcService), AsyncProductThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncWaferTypeThread(gcService), AsyncWaferTypeThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncProductModelConversionThread(gcService), AsyncProductModelConversionThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncProductRelationThread(gcService), AsyncProductRelationThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new AsyncPoSupplierThread(gcService), AsyncPoSupplierThread.CRON_EXPRESS);
        scheduledTaskRegistrar.addCronTask(new RetryInterfaceThread(gcService, scmService), RetryInterfaceThread.CRON_EXPRESS);

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
