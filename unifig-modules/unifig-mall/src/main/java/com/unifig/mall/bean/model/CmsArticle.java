package com.unifig.mall.bean.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文章表
 * </p>
 *
 *
 * @since 2019-01-22
 */
@TableName("cms_article")
@ApiModel
public class CmsArticle extends Model<CmsArticle> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 类别ID
     */
    @TableField("cat_id")
    private String catId;

    /**
     * 类别name
     */
    @TableField("cat_name")
    private String catName;


    @TableField("product_id")
    private Long productId;
    /**
     * 文章标题
     */
    private String title;
    private String content;
    /**
     * 文章作者
     */
    private String author;
    /**
     * 作者邮箱
     */
    @TableField("author_email")
    private String authorEmail;
    /**
     * 关键字
     */
    private String keywords;
    @TableField("article_type")
    private Integer articleType;
    @TableField("is_open")
    private Integer isOpen;
    @TableField("add_time")
    private Integer addTime;
    /**
     * 附件地址
     */
    @TableField("file_url")
    private String fileUrl;
    @TableField("open_type")
    private Integer openType;
    /**
     * 链接地址
     */
    private String link;
    /**
     * 文章摘要
     */
    private String description;

    /**
     * 点赞量
     */
    private Integer like;
    /**
     * 点赞所得积分
     */
    @TableField("like_integral")
    private Integer likeIntegral;

    /**
     * 浏览量
     */
    private Integer click;
    /**
     * 浏览所得积分
     */
    @TableField("click_integral")
    private Integer clickIntegral;

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
     * 文章预告发布时间
     */
    @TableField("publish_time")
    private Date publishTime;
    /**
     * 文章缩略图
     */
    private String thumb;

    private Integer money;
    @TableField("c_money")
    private Float cMoney;
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
     * 0=删除 1=可用
     */
    private Integer enable;

    /**
     * 是否显示在导航栏：0->不显示；1->显示
     *
     * @mbggenerated
     */
    @TableField("nav_status")
    private Integer navStatus;

    /**
     * 商品列表
     */
    @TableField(exist = false)
    private List<CmsArticleProduct> productList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getArticleType() {
        return articleType;
    }

    public void setArticleType(Integer articleType) {
        this.articleType = articleType;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getAddTime() {
        return addTime;
    }

    public void setAddTime(Integer addTime) {
        this.addTime = addTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getClick() {
        return click;
    }

    public void setClick(Integer click) {
        this.click = click;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Float getcMoney() {
        return cMoney;
    }

    public void setcMoney(Float cMoney) {
        this.cMoney = cMoney;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getShare() {
        return share;
    }

    public void setShare(Integer share) {
        this.share = share;
    }

    public Integer getClickIntegral() {
        return clickIntegral;
    }

    public void setClickIntegral(Integer clickIntegral) {
        this.clickIntegral = clickIntegral;
    }

    public String getShareIntegral() {
        return shareIntegral;
    }

    public void setShareIntegral(String shareIntegral) {
        this.shareIntegral = shareIntegral;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public List<CmsArticleProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<CmsArticleProduct> productList) {
        this.productList = productList;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Integer getLikeIntegral() {
        return likeIntegral;
    }

    public void setLikeIntegral(Integer likeIntegral) {
        this.likeIntegral = likeIntegral;
    }

    public Integer getNavStatus() {
        return navStatus;
    }

    public void setNavStatus(Integer navStatus) {
        this.navStatus = navStatus;
    }

    @Override
    public String toString() {
        return "CmsArticle{" +
                ", id=" + id +
                ", catId=" + catId +
                ", title=" + title +
                ", content=" + content +
                ", author=" + author +
                ", authorEmail=" + authorEmail +
                ", keywords=" + keywords +
                ", articleType=" + articleType +
                ", isOpen=" + isOpen +
                ", addTime=" + addTime +
                ", fileUrl=" + fileUrl +
                ", openType=" + openType +
                ", link=" + link +
                ", description=" + description +
                ", click=" + click +
                ", publishTime=" + publishTime +
                ", thumb=" + thumb +
                ", money=" + money +
                ", cMoney=" + cMoney +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", ratelNo=" + ratelNo +
                "}";
    }
}
