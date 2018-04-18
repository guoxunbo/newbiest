package com.newbiest.base.aop;

import com.google.common.base.Stopwatch;
import com.newbiest.base.annotation.MethodMonitor;
import com.newbiest.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 方法执行效率监控的建言
 * Created by guoxunbo on 2017/11/17.
 */
@Aspect
@Component
@Slf4j
public class MethodMonitorAdvice {

    @Around("@annotation(methodMonitor)")
    public Object invoke(ProceedingJoinPoint joinPoint, MethodMonitor methodMonitor) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String name = StringUtils.isEmpty(methodMonitor.value()) ? methodSignature.getName() : methodMonitor.value();

        Stopwatch stopwatch = Stopwatch.createStarted();
        Object object = joinPoint.proceed();
        stopwatch.stop();
        Long executeTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (log.isInfoEnabled()) {
            log.info("The name [" + name + "] spend [ " + executeTime + TimeUnit.MILLISECONDS + "]");
        }

        return object;
    }


}
