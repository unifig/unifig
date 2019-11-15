package com.unifig.organ.feign;

import com.unifig.model.CartPromotionItem;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Component
@FeignClient(value = "unifig-mall")
public interface SandCartFeignClient {

    @GetMapping(value = "/cart/listPromotion")
    List<CartPromotionItem>  listPromotion(Long userId);
}
