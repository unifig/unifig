package etl.dispatch.config.entity;

import etl.dispatch.config.bean.BaseManagedEntity;

public class ConfRelyGroupEntity extends BaseManagedEntity {
	private static final long serialVersionUID = -6875696092411584663L;

	private Integer pkId;

	private Integer groupId;

	private Integer relyGroupId;

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

	public Integer getRelyGroupId() {
		return relyGroupId;
	}

	public void setRelyGroupId(Integer relyGroupId) {
		this.relyGroupId = relyGroupId;
	}
}
