package etl.dispatch.java.ods.domain;

import java.io.Serializable;

public class DimCountry implements Serializable {
	private static final long serialVersionUID = -2411049953409015910L;

	private int id;

	private String countryCode;

	private String name;

	public DimCountry() {

	}

	public DimCountry(int id, String countryCode, String name) {
		this.id = id;
		this.countryCode = countryCode;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
			DimCountry cloned = (DimCountry) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimCountry(this.id, this.countryCode, this.name);
		}
	}
}
