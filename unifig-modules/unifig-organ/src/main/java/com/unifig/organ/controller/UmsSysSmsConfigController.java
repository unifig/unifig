package com.unifig.organ.controller;

import com.unifig.organ.service.UmsSysConfigService;
import com.unifig.entity.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 系统用户
 *

 * @date 2018-10-24
 */
@RestController
@RequestMapping("/sys/config")
@ApiIgnore
public class UmsSysSmsConfigController extends AbstractController {
    @Autowired
    private UmsSysConfigService sysConfigService;

    /**
     * 获取短信的配置
     */
    @PostMapping("/sms")
    public SmsConfig getSmsConfig(String key) {
        SmsConfig smsConfig = sysConfigService.getConfigObject(key, SmsConfig.class);
        return smsConfig;
    }
}
