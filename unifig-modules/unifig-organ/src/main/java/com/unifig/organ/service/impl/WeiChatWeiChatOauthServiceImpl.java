package com.unifig.organ.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.unifig.context.Constants;
import com.unifig.context.RedisConstants;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.UmsMember;
import com.unifig.organ.meta.OAuthWebChatMeta;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.service.WeiChatOauthService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.*;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @date 2018/11/22
 */
@Service
public class WeiChatWeiChatOauthServiceImpl implements WeiChatOauthService {

    private final static Logger logger = LoggerFactory.getLogger(WeiChatWeiChatOauthServiceImpl.class);

    @Autowired
    private CacheRedisUtils oauthCache;

    @Autowired
    private OAuthWebChatMeta oAuthWebChatMeta;

    @Autowired
    private UmsMemberService memberService;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    @Override
    public ResultData login(String code) {

        JSONObject resultJson = reqestCodeFromWebChat(oAuthWebChatMeta, code);
        if (resultJson == null) {
            return ResultData.result(true).setCode(MsgConstants.CODE_FAILURE);
        }
        logger.info("apply session_key body : {}", resultJson.toJSONString());

        String openid = resultJson.getString("openid");
        String sessionKey = resultJson.getString("session_key");

        this.putOpenIdCache(openid, sessionKey);

        if (Objects.isNull(openid) || Objects.isNull(sessionKey)) {
            logger.info("apply session_key failed : {}", sessionKey);
            ResultData.result(true).setCode(MsgConstants.CODE_FAILURE);
        }

        UmsMember binding = memberService.isBinding(openid, Constants.WE_CHAT);
        if (Objects.nonNull(binding)) {
            String token = userCreatToken(binding);
            UserCache userCache = userCreatCache(binding);
            Map<String, Object> resultObj = new HashMap<String, Object>();
            resultObj.put("token", token);
            resultObj.put("userInfo", userCache);
            resultObj.put("userId", binding.getId());
            resultObj.put("tokenHead", tokenHead);
            return ResultData.result(true).setData(resultObj);
        }

        //注册
        JSONObject resJson = new JSONObject();
        resJson.put("openid", openid);
        return ResultData.result(false).setCode(MsgConstants.USER_UNAUTH).setData(resJson);

    }

    @Override
    public ResultData registerUserInfo(String openid, String avatarUrl, String city, String country, String province, Integer gender, String nickName, String language) {

        String json = String.valueOf(oauthCache.get("web_chat" + openid));
        if (Objects.isNull(json)) {
            return ResultData.result(false).setCode(MsgConstants.REGISTERED_IS_TIMEOUT);
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        jsonObject.put("avatarUrl", avatarUrl);
        jsonObject.put("city", city);
        jsonObject.put("country", country);
        jsonObject.put("province", province);
        jsonObject.put("gender", gender);
        jsonObject.put("nickName", nickName);
        jsonObject.put("language", language);

        oauthCache.set("web_chat" + openid, jsonObject.toJSONString(), 30 * 60);
        memberService.register(jsonObject, openid, null, Constants.WE_CHAT);
        JSONObject resJson = new JSONObject();
        resJson.put("openid", jsonObject.get("openid"));
        return ResultData.result(true).setData(resJson);
    }


    @Override
    public ResultData userInfo(String openid, String encryptedData, String iv) {

        String json = String.valueOf(oauthCache.get("web_chat" + openid));
        if (Objects.isNull(json)) {
            return ResultData.result(false).setCode(MsgConstants.REGISTERED_IS_TIMEOUT);
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        String sessionKey = jsonObject.getString("session_key");

        //获取用户信息
        JSONObject userInfo = this.encryptedForUserInfo(encryptedData, sessionKey, iv);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultData.result(false).setCode(MsgConstants.REGISTERED_IS_TIMEOUT);
        }

        userInfo.put("openid", openid);
        UmsMember register = memberService.register(userInfo, openid, null, Constants.WE_CHAT);
        String token = userCreatToken(register);
        UserCache userCache = userCreatCache(register);
        Map<String, Object> resultObj = new HashMap<String, Object>();
        resultObj.put("token", token);
        resultObj.put("userInfo", userCache);
        resultObj.put("userId", register.getId());
        resultObj.put("tokenHead", tokenHead);
        return ResultData.result(true).setData(resultObj);
    }

    @Override
    public ResultData bindingMobile(String openid, String encryptedData, String iv) {
        String json = String.valueOf(oauthCache.get("web_chat" + openid));
        if (StringUtil.isBlankOrNull(json)) {
            return ResultData.result(false).setCode(MsgConstants.REGISTERED_IS_TIMEOUT);
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        String sessionKey = jsonObject.getString("session_key");

        //获取手机信息
        String phoneNumber = null;
        if (!StringUtils.isEmpty(encryptedData)) {
            phoneNumber = this.encryptedForPhoneNumber(encryptedData, sessionKey, iv);
            if (StringUtils.isEmpty(phoneNumber)) {
                return ResultData.result(false).setCode(MsgConstants.PHONE_UNKNOWN);
            }
        }
        UmsMember umsMember = memberService.getByOpenid(openid);
        umsMember.setMobile(phoneNumber);
        memberService.updateById(umsMember);
        return ResultData.result(true).setData(phoneNumber);
    }


    /**
     * <p>
     * 使用code码从微信获取授权信息
     * </p>
     *
     * @param oAuthWebChatMeta
     * @param code
     * @return
     */
    private JSONObject reqestCodeFromWebChat(OAuthWebChatMeta oAuthWebChatMeta, String code) {

        HashMap<String, Object> params = new HashMap<>(5);
        params.put("appid", oAuthWebChatMeta.getAppid());
        params.put("secret", oAuthWebChatMeta.getSecret());
        params.put("js_code", code);
        params.put("grant_type", oAuthWebChatMeta.getGrantType());

        HttpRequest httpRequest = HttpRequest.get(oAuthWebChatMeta.getUrl()).form(params);

        int httpCode = httpRequest.code();
        if (httpCode == HttpStatus.SC_OK) {
            String body = httpRequest.body();
            JSONObject jsonResult = JSONObject.parseObject(body);
            return jsonResult;
        }
        return null;
    }

    private void putOpenIdCache(String openid, String sessionKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openid", openid);
        jsonObject.put("session_key", sessionKey);
        oauthCache.set("web_chat" + openid, jsonObject.toJSONString(), 60 * 60);
    }


    /**
     * <p>
     * 解密获取手机号
     * </p>
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    private String encryptedForPhoneNumber(String encryptedData, String sessionKey, String iv) {
        byte[] result = DecryptUtil.decrypt(encryptedData, sessionKey, iv);
        JSONObject phoneJsonObject = null;
        try {
            phoneJsonObject = JSONObject.parseObject(new String(result, "UTF-8"));
            return (String) phoneJsonObject.get("phoneNumber");
        } catch (UnsupportedEncodingException e) {
            logger.error("解密微信手机号失败", e);
        }
        return null;
    }


    /**
     * <p>
     * 解密用户信息
     * </p>
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    private JSONObject encryptedForUserInfo(String encryptedData, String sessionKey, String iv) {
        byte[] result = DecryptUtil.decrypt(encryptedData, sessionKey, iv);
        try {
            return JSONObject.parseObject(new String(result, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("解密用户信息失败", e);
        }
        return null;
    }


    private UserCache userCreatCache(UmsMember binding) {
        UserCache userCache = new UserCache(binding);
        userCache.setOpenid(binding.getOpenid());
        userCache.setUserId(String.valueOf(binding.getId()));
        userCache.setUsername(String.valueOf(binding.getNickname()));
        Map<String, Object> userInfoMap = BeanMapUtils.convertBean2Map(userCache, new String[]{"serialVersionUID"});
        cacheRedisUtils.hmset(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_PLAT_USER_KAY).append(binding.getId()).toString(), userInfoMap);
        return userCache;
    }

    private String userCreatToken(UmsMember binding) {
        String token = jwtTokenUtil.generateToken(binding.getId(), binding.getUsername(), Constants.RATEL_PLAT_TAG, binding.getMobile(), binding.getNickname(), binding.getOpenid(), null,String.valueOf(binding.getProxy()));
        return token;
    }
}
