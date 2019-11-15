package com.unifig.mall.bean.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 我的店铺列表
 * </p>
 *
 *
 * @since 2019-02-19
 */
@TableName("pms_user_shop")
public class PmsUserShop extends Model<PmsUserShop> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 商品id
     */
    @TableField("product_id")
    private String productId;
    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;
    /**
     * 状态 0  可用    1 不可用
     */
    private Integer status;
    /**
     * 创建时间(代理时间)
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 获得积分
     */
    private Integer integral;
    /**
     * 销量
     */
    @TableField("sales_volume")
    private Integer salesVolume;
    /**
     * 分享数量
     */
    @TableField("share_number")
    private Integer shareNumber;


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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
    }

    public Integer getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(Integer shareNumber) {
        this.shareNumber = shareNumber;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PmsUserShop{" +
        ", id=" + id +
        ", productId=" + productId +
        ", userId=" + userId +
        ", status=" + status +
        ", createTime=" + createTime +
        ", integral=" + integral +
        ", salesVolume=" + salesVolume +
        ", shareNumber=" + shareNumber +
        "}";
    }
}
