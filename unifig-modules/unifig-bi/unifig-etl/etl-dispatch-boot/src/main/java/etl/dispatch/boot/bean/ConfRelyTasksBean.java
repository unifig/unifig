package etl.dispatch.boot.bean;

import java.io.Serializable;

public class ConfRelyTasksBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9206159417913151153L;
	private int tasksId;
	private String tasksName;
	private int relytasksId;
	private int groupId;
	private String groupName;

	public int getTasksId() {
		return tasksId;
	}

	public void setTasksId(int tasksId) {
		this.tasksId = tasksId;
	}

	public String getTasksName() {
		return tasksName;
	}

	public void setTasksName(String tasksName) {
		this.tasksName = tasksName;
	}

	public int getRelytasksId() {
		return relytasksId;
	}

	public void setRelytasksId(int relytasksId) {
		this.relytasksId = relytasksId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
