package com.unifig.mall.bean.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索中的商品信息
 *    on 2018/6/19.
 */
@Document(indexName = "yirisanxian", type = "product",shards = 1,replicas = 0)
public class EsProduct implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("商品id")
    @Id
    private Long id;

    @ApiModelProperty("商品编号")
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private String productSn;


//    @JsonIgnore
//    @ApiModelProperty("")
    private Long brandId;


//    @JsonIgnore
    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
    private String brandName;


//    @JsonIgnore
    private Long productCategoryId;


    @Field(index = FieldIndex.not_analyzed,type = FieldType.String)
//    @JsonIgnore
    private String productCategoryName;


    @ApiModelProperty("商品图片")
    private String pic;


    @ApiModelProperty("商品名称")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String name;


    @ApiModelProperty("商品副标题")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String subTitle;


    @ApiModelProperty("关键词")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String keywords;


    @ApiModelProperty("价格")
    private BigDecimal price;


//    @ApiModelProperty("")
//    @JsonIgnore
    private Integer sale;


//    @JsonIgnore
    private Integer newStatus;

//    @JsonIgnore
    private Integer recommandStatus;

//    @JsonIgnore
    private Integer stock;

//    @JsonIgnore
    private Integer promotionType;

//    @JsonIgnore
    private Integer sort;

//    @JsonIgnore
    @Field(type =FieldType.Nested)
    private List<EsProductAttributeValue> attrValueList;


//    @JsonIgnore
    private Integer type;

    /**
     * 积分
     */
//    @JsonIgnore
    private Integer usePointLimit;

    /**
     * 原价
     */
    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    /**
     * 商品图册
     */
//    @JsonIgnore
    private String albumPics;

    /**
     * 移动端详情页
     */
//    @JsonIgnore
    private String detailMobileHtml;

    /**
     * 是否团购
     */
//    @JsonIgnore
    private Integer group;

    /**
     * 店铺id
     */
    @ApiModelProperty("店铺id")
    private String shopId;

    /**
     * 平台id
     */
//    @JsonIgnore
    private String terraceId;

    @ApiModelProperty("距离")
    private String distance;

    /**
     * 团购价格
     */
    private BigDecimal groupPurchasePrice;

    public BigDecimal getGroupPurchasePrice() {
        return groupPurchasePrice;
    }

    public void setGroupPurchasePrice(BigDecimal groupPurchasePrice) {
        this.groupPurchasePrice = groupPurchasePrice;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getTerraceId() {
        return terraceId;
    }

    public void setTerraceId(String terraceId) {
        this.terraceId = terraceId;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getAlbumPics() {
        return albumPics;
    }

    public void setAlbumPics(String albumPics) {
        this.albumPics = albumPics;
    }

    public String getDetailMobileHtml() {
        return detailMobileHtml;
    }

    public void setDetailMobileHtml(String detailMobileHtml) {
        this.detailMobileHtml = detailMobileHtml;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getUsePointLimit() {
        return usePointLimit;
    }

    public void setUsePointLimit(Integer usePointLimit) {
        this.usePointLimit = usePointLimit;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public Integer getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Integer newStatus) {
        this.newStatus = newStatus;
    }

    public Integer getRecommandStatus() {
        return recommandStatus;
    }

    public void setRecommandStatus(Integer recommandStatus) {
        this.recommandStatus = recommandStatus;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<EsProductAttributeValue> getAttrValueList() {
        return attrValueList;
    }

    public void setAttrValueList(List<EsProductAttributeValue> attrValueList) {
        this.attrValueList = attrValueList;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
