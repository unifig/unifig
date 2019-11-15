package com.unifig.organ;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@MapperScan(value = {"com.unifig.organ.mapper","com.unifig.organ.dao"})
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@EnableSwagger2
@ComponentScan({"com.unifig.interceptor","com.unifig.config","com.unifig.utils","com.unifig.organ.utils","com.unifig.organ.cache","com.unifig.organ.service","com.unifig.organ.feign","com.unifig.organ.controller","com.unifig.component","com.unifig.organ.config"})
public class OrganApplication {

    public static void main(String[] args) {
        System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","|{}");
        SpringApplication.run(OrganApplication.class, args);
    }

}
