package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * 文章表
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Data
@ToString
public class HomeCmsArticleVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文章id")
    private String id;

    /**
     * 文章标题
     */
    @ApiModelProperty("文章标题")
    private String title;

    /**
     * 文章摘要
     */
    @ApiModelProperty("文章摘要")
    private String description;

    /**
     * 分享量
     */
    @ApiModelProperty("分享量")
    private Integer share;

    /**
     * 点赞量
     */

    @ApiModelProperty("点赞量")
    private Integer like;

    /**
     * 浏览量
     */
    @ApiModelProperty("浏览量")
    private Integer click;

    /**
     * 文章缩略图
     */
    @ApiModelProperty("文章缩略图")
    private String thumb;



}
