package com.newbiest.rms.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by guoxunbo on 2018/7/5.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.newbiest.*")
public class RmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmsApplication.class, args);
    }
}
