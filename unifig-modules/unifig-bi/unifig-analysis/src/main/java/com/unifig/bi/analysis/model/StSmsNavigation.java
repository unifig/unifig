package com.unifig.bi.analysis.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 导航栏
 * </p>
 *
 *
 * @since 2019-03-22
 */
@TableName("st_sms_navigation")
public class StSmsNavigation extends Model<StSmsNavigation> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("dept_id")
    private String deptId;
    @TableField("dept_name")
    private String deptName;
    @TableField("navigation_id")
    private String navigationId;
    @TableField("user_id")
    private String userId;
    @TableField("user_name")
    private String userName;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * 0=删除 1=可用
     */
    private Integer enable;
    @TableField("statis_date")
    private Integer statisDate;
    /**
     * 小时为-1，则统计的为天，小时存在具体到天的小时统计
     */
    private Integer hour;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getNavigationId() {
        return navigationId;
    }

    public void setNavigationId(String navigationId) {
        this.navigationId = navigationId;
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

    public String getRatelNo() {
        return ratelNo;
    }

    public void setRatelNo(String ratelNo) {
        this.ratelNo = ratelNo;
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

    public Integer getStatisDate() {
        return statisDate;
    }

    public void setStatisDate(Integer statisDate) {
        this.statisDate = statisDate;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StSmsNavigation{" +
        ", id=" + id +
        ", deptId=" + deptId +
        ", deptName=" + deptName +
        ", navigationId=" + navigationId +
        ", userId=" + userId +
        ", userName=" + userName +
        ", ratelNo=" + ratelNo +
        ", createTime=" + createTime +
        ", editTime=" + editTime +
        ", enable=" + enable +
        ", statisDate=" + statisDate +
        ", hour=" + hour +
        "}";
    }
}
