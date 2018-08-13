package com.newbiest.common.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by guoxunbo on 2018/8/8.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.newbiest.*")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

