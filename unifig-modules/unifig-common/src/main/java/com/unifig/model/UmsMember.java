package com.unifig.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 会员表
 * </p>
 *
 *
 * @since 2019-06-27
 */
@Data
@TableName("ums_member")
public class UmsMember extends Model<UmsMember> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 推荐人id
     */
    @TableField("recommender_id")
    private String recommenderId;
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
     * 用户来源 0微信 1手机号注册
     */
    @TableField("source_type")
    private Integer sourceType;
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
     * 帐号启用状态:0->禁用；1->启用
     */
    private Integer status;
    @TableField("register_time")
    private Date registerTime;
    /**
     * 0普通用户 1店铺管理者 2代销 3配送人员

     */
    private Integer proxy;
    /**
     * 邀请码
     */
    @TableField("invit_code")
    private String invitCode;
    /**
     * 注册时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 注册时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * terrace id
     */
    @TableField("terrace_id")
    private String terraceId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
