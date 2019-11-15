package com.unifig.organ.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 会员积分成长规则表
 * </p>
 *
 *
 * @since 2019-01-27
 */
@TableName("ums_member_integration_rule_setting")
public class UmsMemberIntegrationRuleSetting extends Model<UmsMemberIntegrationRuleSetting> {

    private static final long serialVersionUID = 1L;

    @TableId(type=IdType.UUID)
    private String id;
    /**
     * 积分增加类型 如  buy_product
     */
    @ApiModelProperty("积分增加类型")
    private String action;
    /**
     * 积分增加描述 购买商品
     */
    @ApiModelProperty("积分增加描述 例如 购买商品 ")
    @TableField("action_info")
    private String actionInfo;
    /**
     * 增加积分的数量
     */
    @ApiModelProperty("增加积分的数量 ")
    private Long integration;
    /**
     * 类型：0->积分规则；1->成长值规则
     */
    @ApiModelProperty("类型：0->积分规则；1->成长值规则")
    private Integer type;
    /**
     * 新增记录时间
     */
    @ApiModelProperty("新增记录时间")
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @ApiModelProperty("更新记录时间")
    @TableField("edit_time")
    private Date editTime;
    /**
     * 1可以 0不可以
     */
    @ApiModelProperty("1可以 0不可以")
    private Integer enable;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(String actionInfo) {
        this.actionInfo = actionInfo;
    }

    public Long getIntegration() {
        return integration;
    }

    public void setIntegration(Long integration) {
        this.integration = integration;
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

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "UmsMemberIntegrationRuleSetting{" +
        ", id=" + id +
        ", action=" + action +
        ", actionInfo=" + actionInfo +
        ", integration=" + integration +
        ", type=" + type +
        ", createTime=" + createTime +
        ", editTime=" + editTime +
        ", enable=" + enable +
        "}";
    }
}
