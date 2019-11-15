package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class HomeSmsAdvertiseVo implements Serializable {
    @ApiModelProperty("轮播图id")
    private Long id;
    @ApiModelProperty("轮播图名称")
    private String name;

    /**
     * 轮播位置：0->PC首页轮播；1->app首页轮播 2->小程序首页轮播
     *
     * @mbggenerated
     */
    @ApiModelProperty("轮播图位置 0->PC首页轮播；1->app首页轮播 2->小程序首页轮播")
    private Integer type;

    @ApiModelProperty("轮播图 图片url")
    private String pic;

    /**
     * 链接地址
     *
     * @mbggenerated
     */
    @ApiModelProperty("轮播图跳转url")
    private String url;


    /**
     * 跳转状态 0 商品 1文章 2团购 3活动 4外链
     *
     * @mbggenerated
     */
    private Integer skipType;

    /**
     * 跳转值
     *
     * @mbggenerated
     */
    private String skipValue;



}