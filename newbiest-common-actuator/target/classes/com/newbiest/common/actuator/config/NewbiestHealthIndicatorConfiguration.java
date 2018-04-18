package com.newbiest.common.actuator.config;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by guoxunbo on 2017/11/4.
 */
@Configuration
public class NewbiestHealthIndicatorConfiguration {

    @Autowired
    private HealthAggregator healthAggregator;

    @Bean
    public HealthIndicator healthIndicator() {
        Map<String, HealthIndicator> indicators = Maps.newHashMap();
        indicators.put("helloWord", new MyHealthChecker());
        return new CompositeHealthIndicator(healthAggregator, indicators);
    }

}
