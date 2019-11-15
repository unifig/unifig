/**
 * FileName: 订单配送
 * Author:
 * Date:     2019-07-24
 * Description: 订单配送
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单配送
 * </p>
 *
 *
 * @since 2019-07-24
 */
@Data
@Accessors(chain = true)
@TableName("oms_order_deliveryman")
public class OmsOrderDeliveryman extends Model<OmsOrderDeliveryman> {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id", name = "uuid")
	@TableId(value = "uuid", type = IdType.UUID)
	private String uuid;
    /**
     * 配送员id
     */
	@TableField("deliveryman_id")
	@ApiModelProperty(value = "配送员id", name = "deliverymanId")
	private String deliverymanId;
    /**
     * 配送员名称
     */
	@TableField("deliveryman_name")
	@ApiModelProperty(value = "配送员名称", name = "deliverymanName")
	private String deliverymanName;
    /**
     * 订单id
     */
	@TableField("order_id")
	@ApiModelProperty(value = "订单id", name = "orderId")
	private String orderId;
    /**
     * 用户姓名
     */
	@ApiModelProperty(value = "用户姓名", name = "name")
	private String name;
    /**
     * 手机号
     */
	@TableField("phone_number")
	@ApiModelProperty(value = "手机号", name = "phoneNumber")
	private String phoneNumber;
    /**
     * 省份/直辖市
     */
	@ApiModelProperty(value = "省份/直辖市", name = "province")
	private String province;
    /**
     * 城市
     */
	@ApiModelProperty(value = "城市", name = "city")
	private String city;
    /**
     * 区
     */
	@ApiModelProperty(value = "区", name = "region")
	private String region;
    /**
     * 详细地址(街道)
     */
	@TableField("detail_address")
	@ApiModelProperty(value = "详细地址(街道)", name = "detailAddress")
	private String detailAddress;
    /**
     * 状态 0 配送中   1  已完成
     */
	@ApiModelProperty(value = "状态 0 配送中   1  已完成", name = "status")
	private Integer status;
    /**
     * 创建时间
     */
	@TableField("create_time")
	@ApiModelProperty(value = "创建时间", name = "createTime")
	private Date createTime;
    /**
     * 完成时间
     */
	@TableField("accomplish_time")
	@ApiModelProperty(value = "完成时间", name = "accomplishTime")
	private Date accomplishTime;

	@TableField(exist = false)
	@ApiModelProperty("订单中所包含的商品")
	private List<OmsOrderItem> orderItemList;

	@Override
	protected Serializable pkVal() {
		return this.uuid;
	}

}
