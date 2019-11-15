package com.unifig.logistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(value = {"com.unifig.logistics.mapper", "com.unifig.logistics.dao"})
@EnableTransactionManagement
@EnableScheduling
@EnableSwagger2
@ComponentScan({"com.unifig.interceptor", "com.unifig.logistics.config", "com.unifig.logistics.utils", "com.unifig.logistics.utils", "com.unifig.logistics.service", "com.unifig.logistics.feign", "com.unifig.logistics.controller", "com.unifig.component", "com.unifig.logistics.config"})
public class LogisticsApplication {

    public static void main(String[] args) {
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow", "|{}");
        SpringApplication.run(LogisticsApplication.class, args);
    }

}
