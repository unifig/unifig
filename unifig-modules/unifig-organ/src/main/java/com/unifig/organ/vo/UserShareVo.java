package com.unifig.organ.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 *
 * @email kaixin254370777@163.com
 * @date 2018-12-12 08:03:41
 */
@Data
public class UserShareVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 数据id
     */
    @ApiModelProperty("数据id")
    private String id;

    /**
     * userId
     */
    @ApiModelProperty("分享此二维码的用户id 后台自动获取 前端不用传")
    private String userId;

    /**
     * 跳转页面
     */
    @ApiModelProperty("跳转页面 扫码以后需要跳转的页面")
    private String page = "pages/mall/index/index";
    /**
     * 跳转类型
     */
    @ApiModelProperty("跳转类型-分享的内容  0文章 1商品 2团购")
    private Integer type;

    /**
     * 二维码
     */

    @ApiModelProperty("二维码数据 前端不用关心")
    private byte[] bytes;
}
