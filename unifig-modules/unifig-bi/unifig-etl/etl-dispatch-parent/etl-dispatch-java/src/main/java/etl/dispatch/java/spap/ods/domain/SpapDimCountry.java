package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

public class SpapDimCountry implements Serializable {
	private static final long serialVersionUID = -2411049953409015910L;

	private int id;

	private String countryCode;

	private String name;

	public SpapDimCountry() {

	}

	public SpapDimCountry(int id, String countryCode, String name) {
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
			SpapDimCountry cloned = (SpapDimCountry) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimCountry(this.id, this.countryCode, this.name);
		}
	}
}
