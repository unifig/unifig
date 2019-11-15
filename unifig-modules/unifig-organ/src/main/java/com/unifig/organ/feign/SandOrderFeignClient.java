package com.unifig.organ.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Component
@FeignClient(value = "unifig-mall")
public interface SandOrderFeignClient {


    @GetMapping(value = "/order/clientListStatistics")
    Map<String,Integer> clientListStatistics();


}
