package com.unifig.rc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class RcApplication {

	public static void main(String[] args) {
		SpringApplication.run(RcApplication.class, args);
	}

}
