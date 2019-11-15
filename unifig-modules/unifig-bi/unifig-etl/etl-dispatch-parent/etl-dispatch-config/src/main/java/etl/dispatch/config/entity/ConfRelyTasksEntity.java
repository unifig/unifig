package etl.dispatch.config.entity;

import etl.dispatch.config.bean.BaseManagedEntity;

public class ConfRelyTasksEntity extends BaseManagedEntity {
	private static final long serialVersionUID = -6875696092411584663L;

	private Integer pkId;

	private Integer groupId;

	private Integer tasksId;

	private Integer relytasksId;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getTasksId() {
		return tasksId;
	}

	public void setTasksId(Integer tasksId) {
		this.tasksId = tasksId;
	}

	public Integer getRelytasksId() {
		return relytasksId;
	}

	public void setRelytasksId(Integer relytasksId) {
		this.relytasksId = relytasksId;
	}

	@Override
	public String toString() {
		return "ConfRelyTasksEntity [pkId=" + pkId + ", groupId=" + groupId + ", tasksId=" + tasksId + ", relytasksId=" + relytasksId + "]";
	}
}
