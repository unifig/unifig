package com.unifig.organ.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @email kaixin254370777@163.com
 * @date 2018-01-27 08:03:41
 */
@Data
public class UserIndexVo {

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    private String nickname;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String avatar;

    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private Integer gender;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String mobile;

    /**
     * 粉丝数量
     */
    @ApiModelProperty("粉丝数量")
    private Integer fans=0;

    /**
     * 关注数量
     */
    @ApiModelProperty("关注数量")
    private Integer follow=0;

    /**
     * 邀请码
     */
    @ApiModelProperty("邀请码")
    private String invitCode;

    /**
     * 收藏个数(商品)
     */
    @ApiModelProperty("收藏个数(商品)")
    private Integer productCollectionCount=0;

    /**
     * 收藏个数(文章)
     */
    @ApiModelProperty("收藏个数(文章)")
    private Integer articleCollectionCount=0;

    /**
     * 积分数量
     */
    @ApiModelProperty("可用积分数量")
    private Integer integration=0;
    @ApiModelProperty("积分总数量")
    private Integer integrationCount=0;

    /**
     * 优惠卷数量
     */
    @ApiModelProperty("优惠卷数量")
    private Integer coupon=0;


    /**
     * 钱包余额
     */
    @ApiModelProperty("钱包余额")
    private Double balance=0d;


    /**
     * 红包余额
     */
    @ApiModelProperty("红包余额")
    private Double reward=0d;


    /**
     * 待付款 订单
     */
    @ApiModelProperty("待付款 订单")
    private Integer payWait=0;

    /**
     * 待发货 订单
     */
    @ApiModelProperty("待发货 订单")
    private Integer payAfter=0;

    /**
     * 待收货 订单
     */
    @ApiModelProperty("待收货 订单")
    private Integer receivingWait=0;

    /**
     * 待评价 订单
     */
    @ApiModelProperty("待评价 订单")
    private Integer  evaluateWait=0;


    /**
     * 售后 订单
     */
    @ApiModelProperty("售后 订单")
    private Integer retreatWait=0;

    /**
     * 发布
     */
    @ApiModelProperty("发布")
    private Integer release=0;
}
