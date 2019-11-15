package com.unifig.mall.bean.model;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 商品团购表
 * </p>
 *
 *
 * @since 2019-01-23
 */
@TableName("pms_group_buying")
@ApiModel(description = "商品团购表")
public class PmsGroupBuying extends Model<PmsGroupBuying> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 团购名称
     */
    @ApiModelProperty(value = "团购名称")
    private String name;

    /**
     * 商品id
     */
    @TableField("product_name")
    @ApiModelProperty(value = "商品名称")
    private String productName;
    /**
     * 商品id
     */
    @TableField("product_id")
    @ApiModelProperty(value = "商品id")
    private String productId;
    /**
     * 开始时间
     */
    @TableField("start_time")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    /**
     * 成团有效时间
     */
    @TableField("valid_time")
    @ApiModelProperty(value = "成团有效时间")
    private Integer validTime;
    /**
     * 开团人数
     */
    @ApiModelProperty(value = "开团人数")
    private Integer number;
    /**
     * 优惠比例
     */
    @TableField("discount_rate")
    @ApiModelProperty(value = "优惠比例")
    private Double discountRate;
    /**
     * 团购价格
     */
    @TableField("group_purchase_price")
    @ApiModelProperty(value = "团购价格")
    private BigDecimal groupPurchasePrice;
    /**
     * 每人限购
     */
    @ApiModelProperty(value = "每人限购")
    private Integer limitation;
    /**
     * 当超过拼团时间人数未满是否同意成团  0 否  1 是
     */
    @TableField("overtime_clustering")
    private Integer overtimeClustering;
    /**
     * 成功开团数
     */
    @TableField("success_group_number")
    private Integer successGroupNumber;
    /**
     * 团购状态 0保存  1开启 2 关闭
     */
    private Integer status = 0;
    /**
     * 创建时间
     */
    @TableField("creation_time")
    private Date creationTime;
    /**
     * skuid数组
     */
    @TableField("sku_ids")
    private String skuIds;

    /**
     * 是否删除 0 否  1 删除
     */
    private Integer enable;

    /**
     * 团购规则
     */
    private String regulation;

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getValidTime() {
        return validTime;
    }

    public void setValidTime(Integer validTime) {
        this.validTime = validTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getGroupPurchasePrice() {
        return groupPurchasePrice;
    }

    public void setGroupPurchasePrice(BigDecimal groupPurchasePrice) {
        this.groupPurchasePrice = groupPurchasePrice;
    }

    public Integer getLimitation() {
        return limitation;
    }

    public void setLimitation(Integer limitation) {
        this.limitation = limitation;
    }

    public Integer getOvertimeClustering() {
        return overtimeClustering;
    }

    public void setOvertimeClustering(Integer overtimeClustering) {
        this.overtimeClustering = overtimeClustering;
    }

    public Integer getSuccessGroupNumber() {
        return successGroupNumber;
    }

    public void setSuccessGroupNumber(Integer successGroupNumber) {
        this.successGroupNumber = successGroupNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(String skuIds) {
        this.skuIds = skuIds;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "PmsGroupBuying{" +
        ", id=" + id +
        ", productId=" + productId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", validTime=" + validTime +
        ", number=" + number +
        ", discountRate=" + discountRate +
        ", groupPurchasePrice=" + groupPurchasePrice +
        ", limitation=" + limitation +
        ", overtimeClustering=" + overtimeClustering +
        ", successGroupNumber=" + successGroupNumber +
        ", status=" + status +
        ", creationTime=" + creationTime +
        ", skuIds=" + skuIds +
        "}";
    }
}
