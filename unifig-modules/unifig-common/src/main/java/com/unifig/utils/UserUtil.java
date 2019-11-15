package com.unifig.utils;

import com.unifig.entity.JWTInfoVo;

public class UserUtil {
    private static JWTInfoVo vo = new JWTInfoVo();


    public static JWTInfoVo getVo() {
        return vo;
    }

    public static void setVo(JWTInfoVo vo) {
        UserUtil.vo = vo;
    }

}
