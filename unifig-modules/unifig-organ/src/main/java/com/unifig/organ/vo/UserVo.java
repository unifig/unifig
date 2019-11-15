package com.unifig.organ.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.unifig.model.UmsMember;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @email kaixin254370777@163.com
 * @date 2018-12-12 08:03:41
 */
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别：0->未知；1->男；2->女
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birthday;

    /**
     * 所在国家
     */
    private String country;
    /**
     * 所在省
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 积分
     */
    private Integer integration;

    /**
     * 锁定的积分
     */
    @TableField("lock_integration")
    private Integer lockIntegration;

    private String openid;


    /**
     * 邀请码
     */
    @TableField("invit_code")
    private String invitCode;


    /**
     * terrace id
     */
    @TableField("terrace_id")
    private String terraceId;

    @TableField("register_time")
    private Date registerTime;

    /**
     * 0普通用户 1店铺管理者 2代销

     */
    private Integer proxy;


    public UserVo() {

    }

    public UserVo(UmsMember umsMember) {
        this.id = umsMember.getId();
        this.username = umsMember.getUsername();
        this.nickname = umsMember.getNickname();
        this.password = umsMember.getPassword();
        this.mobile = umsMember.getMobile();
        this.avatar = umsMember.getAvatar();
        this.gender = umsMember.getGender();
        this.birthday = umsMember.getBirthday();
        this.country = umsMember.getCountry();
        this.province = umsMember.getProvince();
        this.city = umsMember.getCity();
        this.integration = umsMember.getIntegration();
        this.openid = umsMember.getOpenid();
        this.invitCode = umsMember.getInvitCode();
        this.terraceId = umsMember.getTerraceId();
        this.registerTime = umsMember.getRegisterTime();
        this.proxy = umsMember.getProxy();
    }
}
