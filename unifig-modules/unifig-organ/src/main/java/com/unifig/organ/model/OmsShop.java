package com.unifig.organ.model;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 店铺表
 * </p>
 *
 *
 * @since 2019-03-11
 */
@ApiModel(value = "店铺表", description = "店铺表")
@TableName("oms_shop")
@Data
public class OmsShop extends Model<OmsShop> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键ID由32位UUID生成", name = "id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 平台id
     */
    @ApiModelProperty("平台id")
    @TableField("terrace_id")
    private String terraceId;
    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    private String name;
    /**
     * logo
     */
    @ApiModelProperty("用户id")
    private String logo;
    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;
    /**
     * 图片
     */
    @ApiModelProperty("图片")
    private String picture;
    /**
     * 地址
     */
    @ApiModelProperty("地址")
    private String site;
    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    /**
     * 维度
     */
    @ApiModelProperty("维度")
    private BigDecimal latitude;
    /**
     * 简介
     */
    @ApiModelProperty("用户id")
    private String intro;
    /**
     * 营业时间
     */
    @ApiModelProperty("营业时间")
    @TableField("business_hours")
    private String businessHours;
    /**
     * 状态 0  正常   1 关闭
     */
    @ApiModelProperty("状态 0  正常   1 关闭")
    private Integer status;
    /**
     * 开店时间
     */
    @ApiModelProperty("开店时间")
    @TableField("create_time")
    private Date createTime;
    /**
     * 省
     */
    @ApiModelProperty("省")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty("市")
    private String city;
    /**
     * 县(区)
     */
    @ApiModelProperty("县(区)")
    private String area;
    /**
     * 类型 0 普通店铺 1 自营店铺
     */
    @ApiModelProperty("类型 0 普通店铺 1 自营店铺")
    private Integer type;

    /**
     * 员工列表
     */
    @ApiModelProperty("员工列表")
    @TableField(exist=false)
    private List<OmsShopStaff> shopStaffLists;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
