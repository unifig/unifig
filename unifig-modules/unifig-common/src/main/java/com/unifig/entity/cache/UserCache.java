package com.unifig.entity.cache;

import com.unifig.model.UmsMember;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户缓存内容
 *

 * @date 2018-10-24
 */
@Data
public class UserCache implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户ID
     */
    private String openid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;


    private String deptId;


    /**
     * 头像
     *
     * @mbggenerated
     */
   private String avatar;


    /**
     * 昵称
     *i
     * @mbggenerated
     */
    private String nickName;

    /**
     * 备注信息
     *
     * @mbggenerated
     */
    private String note;


    private String status;

    private String proxy;

    public UserCache() {

    }

    public UserCache(UmsMember umsMember) {
        this.userId = String.valueOf(umsMember.getId());
        this.openid = umsMember.getOpenid();
        this.username = umsMember.getNickname();
        this.mobile = umsMember.getMobile();
        this.avatar = umsMember.getAvatar();
        this.nickName = umsMember.getNickname();
        this.proxy = String.valueOf(umsMember.getProxy());
    }
}
