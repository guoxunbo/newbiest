package com.newbiest.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbiest.base.msg.DefaultParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication
@ComponentScan(basePackages = "com.newbiest")
public class NewbiestApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewbiestApplication.class, args);
	}

}

