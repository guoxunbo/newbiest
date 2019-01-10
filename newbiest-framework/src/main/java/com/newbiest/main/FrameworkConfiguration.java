package com.newbiest.main;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by guoxunbo on 2019/1/10.
 */
@Configuration
@PropertySource(value = "classpath:framework-${spring.profiles.active}.yml", factory = YmlPropertyLoaderFactory.class)
public class FrameworkConfiguration {
}
