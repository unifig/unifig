package com.unifig.mall.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class PmsSkuStock implements Serializable {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("商品id")
    private Long productId;

    /**
     * sku编码
     *
     * @mbggenerated
     */
    @ApiModelProperty("sku编码")
    private String skuCode;

    @ApiModelProperty("价格")
    private BigDecimal price;

    /**
     * 库存
     *
     * @mbggenerated
     */
    @ApiModelProperty("库存")
    private Integer stock;

    /**
     * 预警库存
     *
     * @mbggenerated
     */
    @ApiModelProperty("预警库存")
    private Integer lowStock;

    /**
     * 销售属性1
     *
     * @mbggenerated
     */
    @ApiModelProperty("销售属性1")
    private String sp1;

    @ApiModelProperty("销售属性2")
    private String sp2;

    @ApiModelProperty("销售属性3")
    private String sp3;

    /**
     * 展示图片
     *
     * @mbggenerated
     */
    @ApiModelProperty("展示图片")
    private String pic;

    /**
     * 销量
     *
     * @mbggenerated
     */
    @ApiModelProperty("销量")
    private Integer sale;

    /**
     * 单品促销价格
     *
     * @mbggenerated
     */
    @ApiModelProperty("单品促销价格")
    private BigDecimal promotionPrice;

    /**
     * 锁定库存
     *
     * @mbggenerated
     */
    @ApiModelProperty("锁定库存")
    private Integer lockStock;

    /**
     * 积分价  积分
     */
    @ApiModelProperty("积分价  积分")
    private Integer integral;

    /**
     * 积分价 金额
     */
    @ApiModelProperty("积分价 金额")
    private BigDecimal integralPrice;

    /**
     * 团购价格
     */
//    @JsonIgnore
    @ApiModelProperty("团购价格")
    private BigDecimal groupPurchasePrice;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getGroupPurchasePrice() {
        return groupPurchasePrice;
    }

    public void setGroupPurchasePrice(BigDecimal groupPurchasePrice) {
        this.groupPurchasePrice = groupPurchasePrice;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public BigDecimal getIntegralPrice() {
        return integralPrice;
    }

    public void setIntegralPrice(BigDecimal integralPrice) {
        this.integralPrice = integralPrice;
    }

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }

    public String getSp1() {
        return sp1;
    }

    public void setSp1(String sp1) {
        this.sp1 = sp1;
    }

    public String getSp2() {
        return sp2;
    }

    public void setSp2(String sp2) {
        this.sp2 = sp2;
    }

    public String getSp3() {
        return sp3;
    }

    public void setSp3(String sp3) {
        this.sp3 = sp3;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Integer getLockStock() {
        return lockStock;
    }

    public void setLockStock(Integer lockStock) {
        this.lockStock = lockStock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", lowStock=").append(lowStock);
        sb.append(", sp1=").append(sp1);
        sb.append(", sp2=").append(sp2);
        sb.append(", sp3=").append(sp3);
        sb.append(", pic=").append(pic);
        sb.append(", sale=").append(sale);
        sb.append(", promotionPrice=").append(promotionPrice);
        sb.append(", lockStock=").append(lockStock);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}