package com.unifig.organ.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 积分变化历史记录表
 * </p>
 *
 *
 * @since 2019-01-28
 */
@TableName("ums_integration_change_history")
public class UmsIntegrationChangeHistory extends Model<UmsIntegrationChangeHistory> {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.UUID)
    private String id;
    @TableField("member_id")
    private String memberId;
    /**
     * 加积分类型
     */
    @ApiModelProperty("加积分类型")
    private String action;
    /**
     * 加积分详情
     */
    @ApiModelProperty("加积分类型描述")
    @TableField("action_info")
    private String actionInfo;
    /**
     * 改变类型：0->增加；1->减少
     */
    @ApiModelProperty("1->增加；0->减少")
    @TableField("change_type")
    private Integer changeType;
    /**
     * 积分改变数量
     */
    @ApiModelProperty("变更数量")
    @TableField("change_count")
    private Integer changeCount;
    /**
     * 操作人员
     */
    @TableField("operate_man")
    private String operateMan;
    /**
     * 操作备注
     */
    @TableField("operate_note")
    private String operateNote;
    /**
     * 积分来源：0->购物；1->管理员修改 2->用户邀请
     */
    @TableField("source_type")
    private Integer sourceType;
    /**
     * 新增记录时间
     */
    @ApiModelProperty("积分添加时间")
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;

    /**
     * 1可以 0不可以
     */
    @ApiModelProperty("当前状态 1可用 0不可用")
    private Integer status;
    /**
     * 1可以 0不可以
     */
    private Integer enable;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }

    public String getOperateMan() {
        return operateMan;
    }

    public void setOperateMan(String operateMan) {
        this.operateMan = operateMan;
    }

    public String getOperateNote() {
        return operateNote;
    }

    public void setOperateNote(String operateNote) {
        this.operateNote = operateNote;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "UmsIntegrationChangeHistory{" +
                ", id=" + id +
                ", memberId=" + memberId +
                ", action=" + action +
                ", actionInfo=" + actionInfo +
                ", changeType=" + changeType +
                ", changeCount=" + changeCount +
                ", operateMan=" + operateMan +
                ", operateNote=" + operateNote +
                ", sourceType=" + sourceType +
                ", createTime=" + createTime +
                ", editTime=" + editTime +
                ", enable=" + enable +
                "}";
    }
}
