package com.unifig.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 优惠券店铺关系
 */
@Data
@ToString
@TableName("sms_coupon_shop_relation")
@ApiModel(description = "优惠券店铺关系")
public class SmsCouponShopsRelation extends Model<SmsCouponShopsRelation> {

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @TableField("coupon_id")
    @ApiModelProperty(value = "优惠券id")
    private Long couponId;

    @TableField("shop_id")
    @ApiModelProperty(value = "店铺id")
    private String  shopId;

    /**
     * 店铺名称
     *
     * @mbggenerated
     */
    @TableField("shop_name")
    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    private static final long serialVersionUID = 1L;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}