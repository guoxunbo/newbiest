package com.newbiest.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.newbiest.msg.DefaultParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.newbiest")
public class NewbiestApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewbiestApplication.class, args);
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = DefaultParser.getObjectMapper();
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}


}

