package etl.dispatch.config.entity;

import java.util.Date;

import etl.dispatch.config.bean.BaseManagedEntity;

public class ConfInfoGroupEntity extends BaseManagedEntity {
	private static final long serialVersionUID = -6875696092411584663L;

	private Integer pkId;

	private String groupName;

	private String tasksCron;

	private String reportNotice;

	private String remark;

	private Date effectiveStart;

	private Date effectiveEnd;

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

	@Override
	public String toString() {
		return "ConfInfoGroupEntity [pkId=" + pkId + ", groupName=" + groupName + ", tasksCron=" + tasksCron + ", reportNotice=" + reportNotice + ", remark=" + remark + ", effectiveStart=" + effectiveStart + ", effectiveEnd=" + effectiveEnd + "]";
	}
}
