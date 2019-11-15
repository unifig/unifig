package com.unifig.mall.feign;

import com.unifig.mall.bean.domain.CommonResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@FeignClient(name = "unifig-organ"/*,url = "https://api.ratelll.com/ro/"*/)
public interface UmsMemberReceiveAddressFeign {

    /**
     * 返回当前用户的收货地址
     */
    @RequestMapping(value = "/member/address/list", method = RequestMethod.GET)
    @ResponseBody
    /*List<UmsMemberReceiveAddress>*/CommonResult list();

    /**
     * 获取地址详情
     * @param id 地址id
     */
    @RequestMapping(value = "/member/address/{id}", method = RequestMethod.GET)
    @ResponseBody
    CommonResult getItem(@PathVariable(value="id") Long id);
}
