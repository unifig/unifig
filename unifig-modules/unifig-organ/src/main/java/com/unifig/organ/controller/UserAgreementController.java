package com.unifig.organ.controller;

import com.unifig.organ.constant.RedisConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.CacheRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * userAgreement
 */
@RestController
@RequestMapping("/sys/userAgreement")
@ApiIgnore
public class UserAgreementController {


    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    @PostMapping("/save")
    public ResultData save(String doc) {
        boolean set = cacheRedisUtils.set(RedisConstants.unifig_SYS + ":userAgreement", doc);
        return ResultData.result(true).setMsg("true").setData(set);
    }

    @PostMapping("/get")
    public ResultData get() {
        Object o = cacheRedisUtils.get(RedisConstants.unifig_SYS + ":userAgreement");
        return ResultData.result(true).setMsg("true").setData(o.toString());
    }
}
