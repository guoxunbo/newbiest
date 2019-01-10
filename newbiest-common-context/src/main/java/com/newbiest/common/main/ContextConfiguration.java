package com.newbiest.common.main;

import com.newbiest.main.YmlPropertyLoaderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:context-${spring.profiles.active}.yml", factory = YmlPropertyLoaderFactory.class)
public class ContextConfiguration {

}

