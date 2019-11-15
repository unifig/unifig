package com.unifig.mall.bean.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文章商品关联表
 * </p>
 *
 *
 * @since 2019-02-18
 */
@TableName("cms_article_product")
public class CmsArticleProduct extends Model<CmsArticleProduct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 文章id
     */
    @TableField("article_id")
    private String articleId;
    /**
     * 商品id
     */
    @TableField("product_id")
    private String productId;
    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;


    /**
     * 商品分类id
     */
    @TableField("product_cat_id")
    private String productCatId;

    /**
     * 商品分类名称
     */
    @TableField("product_cat_name")
    private String productCatName;


    /**
     * 商品图片
     */
    @TableField("product_pic")
    private String productPic;
    /**
     * 商品标题
     */
    @TableField("product_subTitle")
    private String productSubtitle;
    /**
     * 商品价格
     */
    @TableField("product_price")
    private String productPrice;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
    /**
     * 1可以 0不可以
     */
    private Integer enable;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public String getProductSubtitle() {
        return productSubtitle;
    }

    public void setProductSubtitle(String productSubtitle) {
        this.productSubtitle = productSubtitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getRatelNo() {
        return ratelNo;
    }

    public void setRatelNo(String ratelNo) {
        this.ratelNo = ratelNo;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getProductCatId() {
        return productCatId;
    }

    public void setProductCatId(String productCatId) {
        this.productCatId = productCatId;
    }

    public String getProductCatName() {
        return productCatName;
    }

    public void setProductCatName(String productCatName) {
        this.productCatName = productCatName;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CmsArticleProduct{" +
                ", id=" + id +
                ", articleId=" + articleId +
                ", productId=" + productId +
                ", productName=" + productName +
                ", productPic=" + productPic +
                ", productSubtitle=" + productSubtitle +
                ", productPrice=" + productPrice +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", ratelNo=" + ratelNo +
                ", enable=" + enable +
                "}";
    }
}
