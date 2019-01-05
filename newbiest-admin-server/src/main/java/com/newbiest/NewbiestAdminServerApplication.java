package com.newbiest;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class NewbiestAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewbiestAdminServerApplication.class, args);
	}

}

