package com.newbiest.rms.main;

import com.newbiest.main.YmlPropertyLoaderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:rms-${spring.profiles.active}.yml", factory = YmlPropertyLoaderFactory.class)
public class RmsConfiguration {

}

