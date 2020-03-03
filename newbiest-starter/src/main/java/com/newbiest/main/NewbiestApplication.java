package com.newbiest.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.google.common.collect.Lists;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.msg.DefaultParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = "com.newbiest")
public class NewbiestApplication {

	public static final String LIQUIBASE_SCAN_PROPERTY_NAME = "liquibase.scan.packages";

	public static void main(String[] args) {
		addLiquibaseScanPackages();
		SpringApplication.run(NewbiestApplication.class, args);
	}

	private static void addLiquibaseScanPackages() {

		List<String> packageScans = Lists.newArrayList();
		packageScans.add("liquibase.change");
		packageScans.add("liquibase.changelog");
		packageScans.add("liquibase.database");
		packageScans.add("liquibase.parser");
		packageScans.add("liquibase.precondition");
		packageScans.add("liquibase.datatype");
		packageScans.add("liquibase.serializer");
		packageScans.add("liquibase.sqlgenerator");
		packageScans.add("liquibase.executor");
		packageScans.add("liquibase.snapshot");
		packageScans.add("liquibase.logging");
		packageScans.add("liquibase.diff");
		packageScans.add("liquibase.structure");
		packageScans.add("liquibase.structurecompare");
		packageScans.add("liquibase.lockservice");
		packageScans.add("liquibase.sdk.database");
		packageScans.add("liquibase.ext");
		packageScans.add("com.newbiest.liquibase,liquibase");
		System.setProperty(LIQUIBASE_SCAN_PROPERTY_NAME, StringUtils.join(packageScans, ","));
		
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = DefaultParser.getObjectMapper();
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}


}

