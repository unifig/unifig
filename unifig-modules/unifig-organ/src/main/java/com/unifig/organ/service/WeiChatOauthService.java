package com.unifig.organ.service;


import com.unifig.result.ResultData;

/**
 * @Author wys
 * @date 2018/11/22
 */
public interface WeiChatOauthService {

    ResultData login(String code);

    ResultData registerUserInfo(String openid, String avatarUrl, String city, String country, String province, Integer gender, String nickName, String language);

    ResultData userInfo(String openid, String encryptedData, String iv);

    ResultData bindingMobile(String openid, String encryptedData, String iv);
}
