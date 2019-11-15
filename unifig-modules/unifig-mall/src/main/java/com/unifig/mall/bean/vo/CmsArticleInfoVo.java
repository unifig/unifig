package com.unifig.mall.bean.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.unifig.mall.bean.model.CmsArticleProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 文章表
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Data
public class CmsArticleInfoVo {

    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 类别ID
     */
    @ApiModelProperty("分类id")
    private String catId;

    /**
     * 类别name
     */
    @ApiModelProperty("分类名称")
    private String catName;

    /**
     * 文章标题
     */
    @ApiModelProperty("文章标题")
    private String title;

    /**
     * 文章内容
     */
    @ApiModelProperty("文章内容")
    private String content;

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
     * 分享所得积分
     */
    @ApiModelProperty("分享得积分")
    private String shareIntegral;
    /**
     * 点赞量
     */
    @ApiModelProperty("点赞")
    private Integer like;
    /**
     * 点赞所得积分
     */
    @ApiModelProperty("点赞得积分")
    private Integer likeIntegral;

    /**
     * 浏览量
     */
    @ApiModelProperty("浏览量")
    private Integer click;
    /**
     * 浏览所得积分
     */
    @ApiModelProperty("浏览得积分")
    private Integer clickIntegral;

    /**
     * 文章缩略图
     */
    @ApiModelProperty("文章缩略图")
    private String thumb;

    /**
     * 0=删除 1=可用
     */
    private Integer enable;

    /**
     * 商品列表
     */
    @ApiModelProperty("商品列表")
    private List<CmsArticleProduct> productList;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 链接地址
     */
    private String link;

    private Integer addTime;

    private String fileUrl;

    private Integer navStatus;

}
