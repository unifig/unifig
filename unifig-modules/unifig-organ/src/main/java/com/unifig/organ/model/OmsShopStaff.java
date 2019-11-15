package com.unifig.organ.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 店铺员工表
 * </p>
 *
 *
 * @since 2019-03-11
 */
@ApiModel(value = "店铺员工", description = "店铺员工")
@TableName("oms_shop_staff")
@Data
@ToString
public class OmsShopStaff extends Model<OmsShopStaff> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID由32位UUID生成", name = "id")
    @TableId(value="id", type= IdType.UUID)
    private String id;
    /**
     * 店铺id
     */
    @TableField("shop_id")
    @ApiModelProperty("店铺id")
    private String shopId;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 账号(手机号)
     */
    @ApiModelProperty("账号(手机号)")
    @TableField("account_number")
    private String accountNumber;


    /**
     * 员工名称
     */
    @ApiModelProperty("员工名称")
    @TableField("name")
    private String name;

    /**
     * 类型  0 普通员工  1  管理员
     */
    @ApiModelProperty("类型  0 普通员工  1  管理员")
    private Integer type;
    /**
     * 状态 0  启用  1 停用  2 解除关联
     */
    @ApiModelProperty("0  启用  1 停用  2 解除关联")
    private Integer status;
    /**
     * 加入时间
     */
    @ApiModelProperty("加入时间")
    @TableField("create_time")
    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
