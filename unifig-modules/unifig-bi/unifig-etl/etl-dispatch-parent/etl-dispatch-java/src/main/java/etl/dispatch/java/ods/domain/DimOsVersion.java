package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 终端平台版本
 *
 */
public class DimOsVersion implements Serializable {
	private static final long serialVersionUID = 5098084818437114039L;
	private short id;
	private short osId;
	private String name;

	public DimOsVersion() {

	}

	public DimOsVersion(short id, short osId, String name) {
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
			DimOsVersion cloned = (DimOsVersion) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimOsVersion(this.id, this.osId, this.name);
		}
	}

}
