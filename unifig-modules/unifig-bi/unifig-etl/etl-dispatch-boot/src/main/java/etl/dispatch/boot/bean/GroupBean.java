package etl.dispatch.boot.bean;

import java.io.Serializable;
import java.util.Date;

public class GroupBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6911288621908935177L;
	private int pkId;
	private String groupName;
	private int groupId;
	private int relygroupId;
	private int relygroupId1;
	private String relygroupName;
	private Integer isSuccess;
	private String message;
	private Date logTime;
	public int getPkId() {
		return pkId;
	}
	public void setPkId(int pkId) {
		this.pkId = pkId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getRelygroupId() {
		return relygroupId;
	}
	public void setRelygroupId(int relygroupId) {
		this.relygroupId = relygroupId;
	}
	public int getRelygroupId1() {
		return relygroupId1;
	}
	public void setRelygroupId1(int relygroupId1) {
		this.relygroupId1 = relygroupId1;
	}
	public String getRelygroupName() {
		return relygroupName;
	}
	public void setRelygroupName(String relygroupName) {
		this.relygroupName = relygroupName;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public Integer getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	
}
