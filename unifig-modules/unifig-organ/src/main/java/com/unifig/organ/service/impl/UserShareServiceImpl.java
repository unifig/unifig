package com.unifig.organ.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.unifig.context.RedisKeyGenerate;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.service.UserShareService;
import com.unifig.organ.utils.AccessToken;
import com.unifig.organ.utils.WxUtils;
import com.unifig.organ.vo.UserShareVo;
import com.unifig.utils.CacheRedisUtils;
import com.unifig.utils.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * <p>
 * 分享业务管理类
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Service
public class UserShareServiceImpl implements UserShareService {

    @Autowired
    private CacheRedisUtils cacheRedisUtils;
    @Autowired
    private WxUtils wxUtils;

    @Override
    public byte[] miniqrQrCode(UserCache userCache, UserShareVo userShareVo) throws IOException {
        //1放入缓存   用户id + 数据id 和 code进行绑定
        userShareVo.setUserId(userCache.getUserId());
        String codeKey = RedisKeyGenerate.shareOrcodeCacheKey(userCache.getUserId(), userShareVo.getId());
        //查询用户是否分享过此数据
        String codeKeyRedis = (String) cacheRedisUtils.get(codeKey);
        if (codeKeyRedis != null) {
            String shareCacheKey = RedisKeyGenerate.shareCacheKey(codeKeyRedis);
            String dataString = (String) cacheRedisUtils.get(shareCacheKey);
            if (dataString != null) {
                UserShareVo userShareVoRedis = JSONObject.toJavaObject(JSONObject.parseObject(dataString), UserShareVo.class);
                return userShareVoRedis.getBytes();
            }
        }

        String code = CodeUtil.generateShortUuid();
        //生成code码
        AccessToken accessToken = wxUtils.getAccessToken();
        byte[] bytes = wxUtils.getminiqrQr(code, userShareVo.getPage(), accessToken.getToken());


        String t = new String(bytes, "utf-8");
        if (t.contains("errcode")) {
            return null;
        }
        cacheRedisUtils.set(codeKey, code);
        userShareVo.setBytes(bytes);
        //关联数据放入缓存
        String shareCacheKey = RedisKeyGenerate.shareCacheKey(code);
        cacheRedisUtils.set(shareCacheKey, JSONObject.toJSONString(userShareVo));
        return bytes;

    }

    @Override
    public UserShareVo sanOrcode(String code) {
        String shareCacheKey = RedisKeyGenerate.shareCacheKey(code);
        String cache = (String) cacheRedisUtils.get(shareCacheKey);
        if (cache != null) {
            UserShareVo userShareVoRedis = JSONObject.toJavaObject(JSONObject.parseObject(cache), UserShareVo.class);
            return userShareVoRedis;
        }
        return null;
    }

}
