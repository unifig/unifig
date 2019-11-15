package etl.dispatch.config.entity;

import etl.dispatch.config.bean.BaseManagedEntity;

public class ConfInfoTasksEntity extends BaseManagedEntity {
	private static final long serialVersionUID = -6875696092411584663L;

	private Integer pkId;

	private String tasksName;

	private String remark;

	private Integer scriptType;

	private Integer scriptId;

	private Integer takeEval;
	
	private String alarmNotice;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public String getTasksName() {
		return tasksName;
	}

	public void setTasksName(String tasksName) {
		this.tasksName = tasksName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getScriptType() {
		return scriptType;
	}

	public void setScriptType(Integer scriptType) {
		this.scriptType = scriptType;
	}

	public Integer getScriptId() {
		return scriptId;
	}

	public void setScriptId(Integer scriptId) {
		this.scriptId = scriptId;
	}

	public Integer getTakeEval() {
		return takeEval;
	}

	public void setTakeEval(Integer takeEval) {
		this.takeEval = takeEval;
	}

	public String getAlarmNotice() {
		return alarmNotice;
	}

	public void setAlarmNotice(String alarmNotice) {
		this.alarmNotice = alarmNotice;
	}
}
