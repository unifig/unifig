package com.unifig.organ.controller;

import com.unifig.organ.service.WeiChatOauthService;
import com.unifig.organ.vo.UserInfoEncryptedVo;
import com.unifig.result.MsgCode;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * ${todo}
 * </p>
 *
 *
 * @date 2018.11.20
 */
@RestController
@RequestMapping("/oauth/wx")
@Api(tags = "微信授权-20190701")
public class WeiChatLoginController {

    @Autowired
    private WeiChatOauthService weiChatOauthService;


    /**
     * 根据code 码登录接口
     *
     * @param code
     * @return
     */
        @GetMapping("/login")
    public ResultData login(@RequestParam("code") String code) {
        if (StringUtils.isEmpty(code)) {
            return ResultData.result(false).setCode(MsgConstants.PARAM_MISS);
        }
        try {
            return weiChatOauthService.login(code);
        } catch (Exception e) {
            e.printStackTrace();
            return  ResultData.result(false);
        }
    }


    /**
     * 终端校验成功会上传用户信息
     *
     * @param encrypted
     * @return
     */
    @PostMapping("/user/info")
    public ResultData loginUserInfo(
            @RequestBody UserInfoEncryptedVo encrypted
    ) {
        if (StringUtils.isEmpty(encrypted.getOpenid()) || StringUtils.isEmpty(encrypted.getEncryptedData()) || StringUtils.isEmpty(encrypted.getIv())) {
            return ResultData.result(false).setCode(MsgCode.PARAM_MISS.getCode());
        }
        try {
            return weiChatOauthService.userInfo(encrypted.getOpenid(), encrypted.getEncryptedData(), encrypted.getIv());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.result(false);
        }
    }

    /**
     * 微信登陆自动获取手机号
     * @param encrypted
     * @return
     */
    @PostMapping("/user/auto/phone")
    public ResultData autoLoginPhone( @RequestBody UserInfoEncryptedVo encrypted
    ) {
        if (StringUtils.isEmpty(encrypted.getOpenid()) || StringUtils.isEmpty(encrypted.getEncryptedData()) || StringUtils.isEmpty(encrypted.getIv())) {
            return  ResultData.result(false).setCode(MsgConstants.PARAM_MISS);
        }
        try {
            return weiChatOauthService.bindingMobile(encrypted.getOpenid(), encrypted.getEncryptedData(), encrypted.getIv());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.result(false);
        }
    }

}
