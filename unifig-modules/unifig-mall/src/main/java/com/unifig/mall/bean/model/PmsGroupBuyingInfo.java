package com.unifig.mall.bean.model;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.unifig.mall.bean.vo.JoinUserVo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品团购子表
 * </p>
 *
 *
 * @since 2019-01-23
 */
@TableName("pms_group_buying_info")
public class PmsGroupBuyingInfo extends Model<PmsGroupBuyingInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.UUID)
    @ApiModelProperty("团购id")
    private String id;
    /**
     * 团购表id
     */
    @TableField("group_buying_id")
    @ApiModelProperty("团购父表id")
    private String groupBuyingId;
    /**
     * 开始时间
     */
    @TableField("start_time")
    @ApiModelProperty("开始时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    @ApiModelProperty("结束时间")
    private Date endTime;
    /**
     * 团购价格
     */
    @TableField("group_purchase_price")
    private BigDecimal groupPurchasePrice;
    /**
     * 团购状态 0拼团中  1拼团成功 2 拼团失败
     */
    @ApiModelProperty("团购状态 0拼团中  1拼团成功 2 拼团失败")
    private Integer status;
    /**
     * 开团人数
     */
    @TableField("success_number")
    @ApiModelProperty("成团人数")
    private Integer successNumber;
    /**
     * 参团人数
     */
    @ApiModelProperty("参团人数")
    private Integer number;

    /**
     * 参团用户列表
     */
    @TableField(exist = false)
    @ApiModelProperty("参团用户列表")
    private List<PmsGroupBuyingUser> userVoList;

    /**
     * 商品id
     */
    @TableField(exist = false)
    @ApiModelProperty("商品id")
    private String productId;

    /**
     * 订单id
     */
    @TableField(exist = false)
    @ApiModelProperty("订单id")
    private String orderId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<PmsGroupBuyingUser> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<PmsGroupBuyingUser> userVoList) {
        this.userVoList = userVoList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupBuyingId() {
        return groupBuyingId;
    }

    public void setGroupBuyingId(String groupBuyingId) {
        this.groupBuyingId = groupBuyingId;
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

    public BigDecimal getGroupPurchasePrice() {
        return groupPurchasePrice;
    }

    public void setGroupPurchasePrice(BigDecimal groupPurchasePrice) {
        this.groupPurchasePrice = groupPurchasePrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSuccessNumber() {
        return successNumber;
    }

    public void setSuccessNumber(Integer successNumber) {
        this.successNumber = successNumber;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PmsGroupBuyingInfo{" +
        ", id=" + id +
        ", groupBuyingId=" + groupBuyingId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", groupPurchasePrice=" + groupPurchasePrice +
        ", status=" + status +
        ", successNumber=" + successNumber +
        ", number=" + number +
        "}";
    }
}
