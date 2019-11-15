package com.unifig.organ.service;

import com.unifig.organ.dto.UmsAdminParam;
import com.unifig.organ.model.SysUserEntity;

/**
 * 后台管理员Service
 *    on 2018/4/26.
 */
public interface UmsAdminService {
    /**
     * 注册功能
     */
    SysUserEntity register(UmsAdminParam umsAdminParam);

    /**
     * 登录功能
     *
     * @param username 用户名
     * @param password 密码
     * @param passwordType
	 * @return 生成的JWT的token
     */
    String login(String username, String password, String passwordType);

    /**
     * 刷新token的功能
     *
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);


    /**
     * 根据手机号查询用户
     *
     * @param mobile
     * @return
     */
    SysUserEntity selectByMobile(String mobile);

    SysUserEntity checkMobile(String mobile);
}
