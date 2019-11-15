package com.unifig.utils;

import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.jsonwebtoken.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
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
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIM_KEY_USERID = "userId";//用户id
    private static final String CLAIM_KEY_USERNAME = "username";//用户名
    private static final String CLAIM_KEY_LOGINTAG = "logintag";//登陆标识  后台管理:admin app,小程序:plat
    private static final String CLAIM_KEY_MOBILE = "mobile";//手机号
    private static final String CLAIM_KEY_CREATED = "created";//创建
    private static final String CLAIM_KEY_NIKENAME = "nikeName";//昵称
    private static final String CLAIM_KEY_OPENID = "openId";//openId
    private static final String CLAIM_KEY_DEPTID = "deptId";//deptId
    private static final String CLAIM_KEY_PROXY = "proxy";//proxy

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    /**
     * 根据负载生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从token中获取JWT中的负载
     */
    private Claims getClaimsFromToken(String token) throws ExpiredJwtException {
        Claims claims = null;
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        return claims;
    }

    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        Date date = new Date(System.currentTimeMillis() + expiration);
        String yyyyMMdd = DateUtils.format(date, "yyyyMMdd");
        return date;
    }

    /**
     * 从token缓存中获取登录用户名
     */
    public String getUserNameFromTCache(String token) {
        String username;
        try {
            Map<Object, Object> userInfo = getUserInfoCache(token);
            username = MapUtils.getString(userInfo, Constants.RATEL_USER_NAME);
            return username;
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            username = getUserName(token);

            return username;
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取登录id
     */
    public String getUserIdFromToken(String token) {
        String userId;
        try {
            userId = getUserId(token);
            return userId;
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserName(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.get("username", String.class);
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserId(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = claims.get("userId", String.class);
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 从tokenCache中获取登录用户名
     */
    public Map<Object, Object> getUserInfoCache(String token) {
        Integer userId;
        String logintag;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = claims.get("userId", Integer.class);
            logintag = claims.get("logintag", String.class);
            if (Constants.RATEL_ADMIN_TAG.equals(logintag)) {
                Map<Object, Object> hmgetUserAdmin = cacheRedisUtils.hmgetUserAdmin(String.valueOf(userId));
                return hmgetUserAdmin;
            } else {
                Map<Object, Object> hmgetUserPlat = cacheRedisUtils.hmgetUserPlat(String.valueOf(userId));
                return hmgetUserPlat;
            }
        } catch (Exception e) {
            e.printStackTrace();
            userId = null;
        }
        return null;
    }

    /**
     * 从tokenCache中获取登录用户名
     */
    public UserCache getUserInfoToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            UserCache userCache = new UserCache();
            userCache.setUserId(String.valueOf(claims.get("userId", Integer.class)));
            userCache.setUsername(String.valueOf(claims.get("username", String.class)));
            userCache.setNickName(String.valueOf(claims.get("nikeName", String.class)));
            userCache.setOpenid(String.valueOf(claims.get("openId", String.class)));
            userCache.setMobile(String.valueOf(claims.get("mobile", String.class)));
            userCache.setDeptId(String.valueOf(claims.get("deptId", String.class)));
            return userCache;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromTCache(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }


    /**
     * 根据用户信息生成token
     */
    public String generateToken(Long userId, String userName, String loginTag, String mobile, String nikeName, String openId, String deptId ,String proxy) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERID, userId);
        claims.put(CLAIM_KEY_USERNAME, userName);
        claims.put(CLAIM_KEY_LOGINTAG, loginTag);
        claims.put(CLAIM_KEY_MOBILE, mobile);
        claims.put(CLAIM_KEY_NIKENAME, nikeName);
        claims.put(CLAIM_KEY_OPENID, openId);
        claims.put(CLAIM_KEY_DEPTID, deptId);
        claims.put(CLAIM_KEY_PROXY, proxy);
        return generateToken(claims);
    }

    /**
     * 判断token是否可以被刷新
     */
    public boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public ResultData checkoutToken(String authToken) {
        String username;
        try {
            Claims claims = getClaimsFromToken(authToken);
            username = claims.get("username", String.class);
            if (username == null)return ResultData.result(false).setCode(MsgConstants.USER_TOKEN_ERROR);

        } catch (ExpiredJwtException e) {
            LOGGER.info("JWT-token 时间失效:{} e:{}", authToken, e.getMessage());
            return ResultData.result(false).setCode(MsgConstants.USER_AUTH_TIMEOUT);

        } catch (SignatureException e){
            LOGGER.info("JWT-token 非法TOKEN:{} e:{}", authToken, e.getMessage());
            return ResultData.result(false).setCode(MsgConstants.USER_TOKEN_ERROR);
        }
        return ResultData.result(true);

    }
}



//<include>
//<!-- UniMRCP Server MRCPv2 -->
//<!-- 后面我们使用该配置文件，均使用 name 作为唯一标识，而不是文件名 -->
//<profile name="unimrcpserver-mrcp2" version="2">
//<!-- MRCP 服务器地址和SIP端口号 -->
//<param name="server-ip" value="192.168.56.165"/>
//<param name="server-port" value="8060"/>
//<param name="resource-location" value=""/>
//
//<!-- FreeSWITCH IP、端口以及 SIP 传输方式 -->
//<param name="client-ip" value="192.168.56.165" />
//<param name="client-port" value="5060"/>
//<param name="sip-transport" value="tcp"/>
//
//<param name="speechsynth" value="speechsynthesizer"/>
//<param name="speechrecog" value="speechrecognizer"/>
//<!--param name="rtp-ext-ip" value="auto"/-->
//<param name="rtp-ip" value="192.168.56.165"/>
//<param name="rtp-port-min" value="4000"/>
//<param name="rtp-port-max" value="5000"/>
//<param name="codecs" value="PCMU PCMA L16/96/8000"/>
//
//<!-- Add any default MRCP params for SPEAK requests here -->
//<synthparams>
//</synthparams>
//
//<!-- Add any default MRCP params for RECOGNIZE requests here -->
//<recogparams>
//<!--param name="start-input-timers" value="false"/-->
//</recogparams>
//</profile>
//</include>
//
//
//
//
//
//
//<extension name="unimrcp">
//<condition field="destination_number" expression="^(50[01][0-9])$">
//<action application="answer"/>
//<action application="lua" data="baidu.lua"/>
//</condition>
//</extension>
//
//
//        session:answer()
//
//        --freeswitch.consoleLog("INFO", "Called extension is '".. argv[1]"'\n")
//        welcome = "ivr/ivr-welcome_to_freeswitch.wav"
//        --
//        grammar = "hello"
//        no_input_timeout = 80000
//        recognition_timeout = 80000
//        --
//
//        tryagain = 1
//        while (tryagain == 1) do
//        --
//        session:execute("play_and_detect_speech",welcome .. "detect:unimrcp {start-input-timers=false,no-input-timeout=" .. no_input_timeout .. ",recognition-timeout=" .. recognition_timeout .. "}" .. grammar)
//        xml = session:getVariable('detect_speech_result')
//        --
//        if (xml == nil) then
//        freeswitch.consoleLog("CRIT","Result is 'nil'\n")
//        tryagain = 0
//        else
//        freeswitch.consoleLog("CRIT","Result is '" .. xml .. "'\n")
//        tryagain = 0
//        end
//        end
//        --
//        -- put logic to forward call here
//        --
//        session:sleep(250)
//        session:hangup()