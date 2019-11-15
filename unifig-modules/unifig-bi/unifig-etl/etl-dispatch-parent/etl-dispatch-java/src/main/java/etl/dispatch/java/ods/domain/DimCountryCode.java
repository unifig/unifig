package etl.dispatch.java.ods.domain;

import java.io.Serializable;

public class DimCountryCode implements Serializable {
	private static final long serialVersionUID = -2411049953409015910L;

	private int id;

	private String code2;

	private String code3;

	private String enName;

	private String cnName;

	public DimCountryCode() {

	}

	public DimCountryCode(int id, String code2, String code3, String enName, String cnName) {
		this.id = id;
		this.code2 = code2;
		this.code3 = code3;
		this.enName = enName;
		this.cnName = cnName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public String getCode3() {
		return code3;
	}

	public void setCode3(String code3) {
		this.code3 = code3;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			DimCountryCode cloned = (DimCountryCode) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimCountryCode(this.id, this.code2, this.code3, this.enName, this.cnName);
		}
	}
}
