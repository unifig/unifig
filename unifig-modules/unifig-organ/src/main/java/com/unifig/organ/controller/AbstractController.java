package com.unifig.organ.controller;

import com.unifig.entity.JWTInfoVo;
import com.unifig.organ.model.SysUserEntity;
import com.unifig.organ.service.UmsSysUserService;
import com.unifig.utils.SpringContextUtils;
import com.unifig.utils.UserUtil;

/**
 * Controller公共组件
 *

 * @date 2018-10-24
 */
public abstract class AbstractController {

    protected SysUserEntity getUser() {
        JWTInfoVo vo = UserUtil.getVo();
        UmsSysUserService sysUserService = SpringContextUtils.getBean(UmsSysUserService.class);
        SysUserEntity sysUserEntity = sysUserService.queryObject(1l);
        sysUserEntity = sysUserService.queryObject(1l);
        return sysUserEntity;
    }

    protected Long getUserId() {
        return getUser().getUserId();
    }

    protected Long getDeptId() {
        return getUser().getDeptId();
    }
}
