package com.unifig.mall.bean.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 用户参团记录
 * </p>
 *
 *
 * @since 2019-06-25
 */
@TableName("pms_group_buying_user")
public class PmsGroupBuyingUser extends Model<PmsGroupBuyingUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 团购id
     */
    @TableField("parent_id")
    private String parentId;
    /**
     * 用户id
     */
    @TableField("user_id")
    @ApiModelProperty("用户id")
    private String userId;
    /**
     * 用户名称
     */
    @TableField("user_name")
    @ApiModelProperty("用户名称")
    private String userName;
    /**
     * 用户头像
     */
    @TableField("user_pic")
    @ApiModelProperty("用户头像")
    private String userPic;
    /**
     * 类型  1  发团人 2 参团人
     */
    @ApiModelProperty("类型  1  发团人 2 参团人")
    private Integer type;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 订单id
     */
    @TableField("order_id")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PmsGroupBuyingUser{" +
        ", id=" + id +
        ", parentId=" + parentId +
        ", userId=" + userId +
        ", userName=" + userName +
        ", userPic=" + userPic +
        ", type=" + type +
        ", createTime=" + createTime +
        "}";
    }
}
