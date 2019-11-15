package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 终端平台版本
 *
 */
public class SpapDimOsVersion implements Serializable {
	private static final long serialVersionUID = 5098084818437114039L;
	private short id;
	private short osId;
	private String name;

	public SpapDimOsVersion() {

	}

	public SpapDimOsVersion(short id, short osId, String name) {
		this.id = id;
		this.osId = osId;
		this.name = name;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public short getOsId() {
		return osId;
	}

	public void setOsId(short osId) {
		this.osId = osId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			SpapDimOsVersion cloned = (SpapDimOsVersion) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimOsVersion(this.id, this.osId, this.name);
		}
	}

}
