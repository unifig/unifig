package etl.dispatch.boot.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 存储任务分组配置
 * </p>
 *
 *
 * @since 2017-08-14
 */
@TableName("conf_info_group")
public class ConfInfoGroup extends Model<ConfInfoGroup> {

    private static final long serialVersionUID = 1L;

    /**
     * 记录自增主键
     */
	@TableId(value="pk_id", type= IdType.AUTO)
	private Integer pkId;
	/**
	 * 分类Id，来源conf_info_classify
	 */
	@TableField("classify_id")
	private Integer classifyId;
    /**
     * 任务分组名称
     */
	@TableField("group_name")
	private String groupName;
    /**
     * Cron任务表达式，若没依赖任务，则使用Cron表达式；若有依赖，依赖任务执行完毕立，才会执行子调度任务，并判断时间是否延期，延期发送告警
     */
	@TableField("tasks_cron")
	private String tasksCron;
    /**
     * json串，执行成功，执行失败，汇报给不同的人 json 2个key节点 ， success，fail 分别汇报给不同人
     */
	@TableField("report_notice")
	private String reportNotice;
    /**
     * 任务分组描述
     */
	private String remark;
    /**
     * 有效开始时间
     */
	@TableField("effective_start")
	private Date effectiveStart;
    /**
     * 有效结束时间
     */
	@TableField("effective_end")
	private Date effectiveEnd;
    /**
     * 1 : 正常 -1：删除 0: 暂停
     */
	private Integer status;
	@TableField("create_user")
	private String createUser;
	@TableField("create_time")
	private Date createTime;
	@TableField("update_user")
	private String updateUser;
	@TableField("update_time")
	private Date updateTime;


	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTasksCron() {
		return tasksCron;
	}

	public void setTasksCron(String tasksCron) {
		this.tasksCron = tasksCron;
	}

	public String getReportNotice() {
		return reportNotice;
	}

	public void setReportNotice(String reportNotice) {
		this.reportNotice = reportNotice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getEffectiveStart() {
		return effectiveStart;
	}

	public void setEffectiveStart(Date effectiveStart) {
		this.effectiveStart = effectiveStart;
	}

	public Date getEffectiveEnd() {
		return effectiveEnd;
	}

	public void setEffectiveEnd(Date effectiveEnd) {
		this.effectiveEnd = effectiveEnd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	

	public Integer getClassifyId() {
		return classifyId;
	}

	public void setClassifyId(Integer classifyId) {
		this.classifyId = classifyId;
	}

	@Override
	protected Serializable pkVal() {
		return this.pkId;
	}

}
