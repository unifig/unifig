package com.unifig.organ.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.vo.UserShareVo;

import java.io.IOException;

/**
 * <p>
 * 分享业务管理类
 * </p>
 *
 *
 * @since 2019-03-06
 */
public interface UserShareService {


    byte[] miniqrQrCode(UserCache userCache, UserShareVo userShareVo) throws IOException;

    UserShareVo sanOrcode(String code);
}
