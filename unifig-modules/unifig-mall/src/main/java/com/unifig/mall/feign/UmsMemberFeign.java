package com.unifig.mall.feign;

import com.unifig.model.UmsMember;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "unifig-organ")
public interface UmsMemberFeign {


    /**
     * 根据用户名获取会员
     */
    @GetMapping("/sso/feign/getByUsername")
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    @GetMapping("/sso/feign/getById")
    UmsMember getById(@RequestParam("id")Long id);


    /**
     * 获取当前登录会员
     */
    @GetMapping("/sso/feign/umemberInfo")
    UmsMember getCurrentMember();

    /**
     * 根据会员id修改会员积分
     */
    @GetMapping("/sso/feign/updateIntegration")
    void updateIntegration(@RequestParam("id") Long id, @RequestParam("integration") Integer integration);

    /**
     * 根据会员id修改会员积分
     */
    @GetMapping("/sso/feign/integralQuery")
    Integer integralQuery();



}
