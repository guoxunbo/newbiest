package com.newbiest.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.newbiest")
public class NewbiestApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewbiestApplication.class, args);
	}

}

