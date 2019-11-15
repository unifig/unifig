package com.unifig.mall.bean.vo;

import com.baomidou.mybatisplus.annotations.TableField;

/**
 * <p>
 * 文章表
 * </p>
 *
 *
 * @since 2019-01-22
 */
public class CmsArticleListVo {

    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 类别ID
     */
    private Integer catId;

    /**
     * 商品id
     */
    private Long productId;
    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String description;

    /**
     * 分享量
     */
    private Integer share;
    /**
     * 分享所得积分
     */
    @TableField("share_integral")
    private String shareIntegral;

    /**
     * 文章缩略图
     */
    private String thumb;

    /**
     * 0=删除 1=可用
     */
    private Integer enable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getShare() {
        return share;
    }

    public void setShare(Integer share) {
        this.share = share;
    }

    public String getShareIntegral() {
        return shareIntegral;
    }

    public void setShareIntegral(String shareIntegral) {
        this.shareIntegral = shareIntegral;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
