package com.unifig.organ.controller;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.unifig.context.Constants;
import com.unifig.context.RedisConstants;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.UmsMember;
import com.unifig.organ.model.FullUserInfo;
import com.unifig.organ.model.UserInfo;
import com.unifig.organ.vo.UserVo;
import com.unifig.organ.service.UmsMemberService;
import com.unifig.organ.utils.ApiBaseAction;
import com.unifig.organ.utils.ApiUserUtils;
import com.unifig.organ.utils.CharUtil;
import com.unifig.organ.utils.CommonUtil;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import com.unifig.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * API登录授权
 *

 * @date 2019-01-16
 */
@Api(tags = "API登录授权接口")
@RestController
@RequestMapping("/api/auth")
@ApiIgnore
public class UmsUserWxAuthController extends ApiBaseAction {
    private Logger logger = Logger.getLogger(getClass());
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation(value = "登录接口")
    public ResultData login(String mobile, String password) {

        //用户登录
        UmsMember login = memberService.login(mobile, password);
        Assert.isNull(login, "手机号或密码错误");
        //密码错误
        if (!login.getPassword().equals(MD5Util.getMD5(password))) {
            return ResultData.result(false).setCode(MsgConstants.USER_PASSWORD_ERROR);
        }
        Map<String, Object> resultObj = new HashMap<String, Object>();
        //生成token
        String token = jwtTokenUtil.generateToken(login.getId(), login.getUsername(), Constants.RATEL_PLAT_TAG, login.getMobile(), login.getNickname(), login.getOpenid(),null,String.valueOf(login.getProxy()));
        resultObj.put("token", token);
        resultObj.put("tokenHead", tokenHead);

        return ResultData.result(true).setData(resultObj);

    }

    /**
     * 登录
     */
    @ApiOperation(value = "登录")
    @PostMapping("login_by_weixin")
    public ResultData loginByWeixin() {
        JSONObject jsonParam = this.getJsonRequest();
        FullUserInfo fullUserInfo = null;
        String code = "";
        if (!StringUtils.isNullOrEmpty(jsonParam.getString("code"))) {
            code = jsonParam.getString("code");
        }
        if (null != jsonParam.get("userInfo")) {
            fullUserInfo = jsonParam.getObject("userInfo", FullUserInfo.class);
        }
        if (null == fullUserInfo) {
            return ResultData.result(false);
        }

        Map<String, Object> resultObj = new HashMap<String, Object>();
        //
        UserInfo userInfo = fullUserInfo.getUserInfo();

        //获取openid
        String requestUrl = ApiUserUtils.getWebAccess(code);//通过自定义工具类组合出小程序需要的登录凭证 code
        logger.info("》》》组合token为：" + requestUrl);
        JSONObject wxData = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null == wxData || StringUtils.isNullOrEmpty(wxData.getString("openid"))) {
            logger.info("wx back wxData:" + wxData.toJSONString());

            return ResultData.result(false).setCode(MsgConstants.USER_WX_OPENID_GET_ERROR);
        }
        /*//验证用户信息完整性
        String sha1 = CommonUtil.getSha1(fullUserInfo.getRawData() + sessionData.getString("session_key"));
        if (!fullUserInfo.getSignature().equals(sha1)) {
            return toResponsFail("登录失败");
        }*/
        Date nowTime = new Date();
        UmsMember  umsMember = memberService.getByOpenid(wxData.getString("openid"));
        if (null == umsMember) {
            umsMember = new UmsMember();
            umsMember.setUsername("微信用户" + CharUtil.getRandomString(12));
            umsMember.setPassword("");
            umsMember.setOpenid(wxData.getString("openid"));
            umsMember.setAvatar(userInfo.getAvatarUrl());
            umsMember.setGender(userInfo.getGender()); // //性别 0：未知、1：男、2：女
            umsMember.setNickname(userInfo.getNickName());
            umsMember.setInvitCode(CodeUtil.genRandomNum());
            umsMember.setRegisterTime(nowTime);
            umsMember.setCreateTime(nowTime);
            umsMember.setEditTime(nowTime);
        }
        memberService.insertOrUpdate(umsMember);
        UserCache userCache = new UserCache(umsMember);
        userCache.setOpenid(umsMember.getOpenid());
        userCache.setUserId(String.valueOf(umsMember.getId()));
        userCache.setUsername(String.valueOf(umsMember.getNickname()));
        String token = jwtTokenUtil.generateToken(umsMember.getId(), umsMember.getUsername(), Constants.RATEL_PLAT_TAG, umsMember.getMobile(), umsMember.getNickname(), umsMember.getOpenid(),null,String.valueOf(umsMember.getProxy()));
        Map<String, Object> userInfoMap = BeanMapUtils.convertBean2Map(userCache, new String[]{"serialVersionUID"});
        cacheRedisUtils.hmset(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_PLAT_USER_KAY).append(umsMember.getId()).toString(), userInfoMap);
        cacheRedisUtils.set(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_PLAT_TOKEN_USER_KAY).append(umsMember.getId()).toString(), token);
        cacheRedisUtils.set(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_PLAT_TOKEN_USER_KAY).append(umsMember.getId()).toString(), token);

        if (null == userInfo || StringUtils.isNullOrEmpty(token)) {
            return ResultData.result(false);
        }

        resultObj.put("token", token);
        resultObj.put("userInfo", userInfo);
        resultObj.put("userId", umsMember.getId());
        resultObj.put("tokenHead", tokenHead);
        return ResultData.result(true).setData(resultObj);
    }


}
