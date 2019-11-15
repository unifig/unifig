package com.unifig.mall.bean.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单售后
 * </p>
 *
 *
 * @since 2019-06-25
 */
@TableName("oms_order_aftersale")
@Data
public class OmsOrderAfterSale extends Model<OmsOrderAfterSale> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 凭证图片，以逗号隔开
     */
    @TableField("proof_pics")
    @ApiModelProperty("凭证图片，以逗号隔开")
    private String proofPics;
    /**
     * 描述
     */
    @TableField("describe")
    @ApiModelProperty("描述")
    private String describe;

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
     * 申请时间
     */
    @TableField("create_time")
    @ApiModelProperty("申请时间")
    private Date createTime;

    /**
     * 订单id
     */
    @TableField("order_id")
    @ApiModelProperty("订单id")
    private String orderId;

    /**
     * 订单编号
     */
    @TableField("order_sn")
    @ApiModelProperty("订单编号")
    private String orderSn;

    /**
     * 最后处理时间
     */
    @TableField("update_time")
    @ApiModelProperty("最后处理时间")
    private Date updateTime;

    /**
     * 类型  0 普通   1 团购
     */
    @ApiModelProperty(" 0 普通   1 团购")
    private Integer type;

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    private BigDecimal money;

    @ApiModelProperty("订单实际支付金额")
    @TableField("order_price")
    private BigDecimal orderPrice;


    @ApiModelProperty("原因")
    private String reason;

    /**
     * 状态
     */
    @ApiModelProperty("状态 0 ->未处理;1->已响应 ;2->已退款 ;3->已拒绝")
    private Integer status;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
