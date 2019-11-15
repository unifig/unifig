package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 菏泽专享 优惠卷视图
 */
@Data
@ToString
public class SmsCouponHistoryVo implements Serializable {

    private Long id;

    @ApiModelProperty("优惠券id")
    private Long couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("用户id")
    private Long memberId;

    /**
     * 领取人昵称
     *
     * @mbggenerated
     */
    @ApiModelProperty("领取人昵称")
    private String memberNickname;

    /**
     * 获取类型：0->后台赠送；1->主动获取
     *
     * @mbggenerated
     */
    @ApiModelProperty("获取类型：0->后台赠送；1->主动获取")
    private Integer getType;

    /**
     * 领取时间
     */
    @ApiModelProperty("领取时间")
    private Date createTime;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    /**
     * 使用状态：0->未使用；1->已使用；2->已过期
     *
     * @mbggenerated
     */
    @ApiModelProperty("使用状态：0->未使用；1->已使用；2->已过期")
    private Integer useStatus;

    /**
     * 使用时间
     *
     * @mbggenerated
     */
    @ApiModelProperty("使用时间")
    private Date useTime;

    /**
     * 核销唯一标识
     */
    @ApiModelProperty("核销码")
    private String code;

    @ApiModelProperty("二维码地址")
    private String url;

    @ApiModelProperty("商家名称")
    private String shopName;

    @ApiModelProperty("商家id")
    private String shopId;

    @ApiModelProperty("商家头像")
    private String shopLogo;

    @ApiModelProperty("商品id")
    private String productId;

    @ApiModelProperty("服务名称")
    private String productName;
}
