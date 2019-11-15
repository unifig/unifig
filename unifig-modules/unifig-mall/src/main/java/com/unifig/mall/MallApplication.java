package com.unifig.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(value = {"com.unifig.mall.dao","com.unifig.mall.mapper"})
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableScheduling
@EnableSwagger2
//@ComponentScan({"com.unifig.utils","com.unifig.config","com.unifig.mall.controller"})
@ComponentScan({"com.unifig.utils","com.unifig.config","com.unifig.mall.config","com.unifig.mall.service","com.unifig.mall.feign","com.unifig.mall.controller","com.unifig.component","com.unifig.mall.component","com.unifig.mall.repository","com.unifig.mall.util","com.unifig.mall.async"})
@EnableAsync
public class MallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
    }
}
