package com.unifig.organ.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UmsMemberReceiveAddress implements Serializable {
    private Long id;

    @ApiModelProperty(value = "地址用户id")
    private Long memberId;

    /**
     * 收货人名称
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "收货人name")
    private String name;
    @ApiModelProperty(value = "收货人电话")
    private String phoneNumber;

    /**
     * 是否为默认  0不是默认 1是默认
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "是否为默认  0不是默认 1是默认")
    private Integer defaultStatus;

    /**
     * 邮政编码
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    /**
     * 省份/直辖市
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "省份/直辖市")
    private String province;

    /**
     * 城市
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
     * 区
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "区")
    private String region;

    /**
     * 详细地址(街道)
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "详细地址(街道)")
    private String detailAddress;

    private static final long serialVersionUID = 1L;


}