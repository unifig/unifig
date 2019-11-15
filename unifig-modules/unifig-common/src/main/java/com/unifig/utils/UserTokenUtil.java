package com.unifig.utils;

import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）：
 * {"alg": "HS512","typ": "JWT"}
 * payload的格式（用户名、创建时间、生成时间）：
 * {"sub":"wang","created":1489079981393,"exp":1489684781}
 * signature的生成算法：
 * HMACSHA256(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 *    on 2018/4/26.
 */
@Component
public class UserTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenUtil.class);
    private static final String CLAIM_KEY_USERID = "userId";//用户id
    private static final String CLAIM_KEY_USERNAME = "username";//用户名
    private static final String CLAIM_KEY_LOGINTAG = "logintag";//登陆标识  后台管理:admin app,小程序:plat
    private static final String CLAIM_KEY_MOBILE = "mobile";//手机号
    private static final String CLAIM_KEY_CREATED = "creat";//创建


    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 去除token中的tokenHead
     */
    private String initToken(String token) {
        try {
            String authToken = token.substring(this.tokenHead.length());// The part after "Bearer "
            return authToken;
        } catch (Exception e) {
            LOGGER.info("JWT格式验证失败:{}", token);
        }
        return null;
    }


    /**
     * 从token中获取user
     */
    public UserCache getUserCacheFromToken(String token) {
        if(token==null)return null;
        UserCache user = new UserCache();
        try {
            String authToken = initToken(token);
            UserCache userCache = jwtTokenUtil.getUserInfoToken(authToken);
            return userCache;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 从token中获取user
     */
    public UserCache getUserCacheFromTokenNoHeade(String token) {
        UserCache user = new UserCache();
        try {
            UserCache userCache = jwtTokenUtil.getUserInfoToken(token);
            return userCache;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 从token中获取user
     */
    public UserCache getUserCacheFromUserMap(Map<Object, Object> userInfo) {
        UserCache user = new UserCache();
        try {
            user.setUserId(MapUtils.getString(userInfo, Constants.RATEL_USER_ID));
            user.setUsername(MapUtils.getString(userInfo, Constants.RATEL_USER_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
