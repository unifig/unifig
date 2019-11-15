package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 省份/地区
 * 
 *
 *
 */
public class SpapDimRegion implements Serializable {
	private static final long serialVersionUID = 1640734798246539965L;

	private int id;

	private int countryId;

	private String name;

	public SpapDimRegion() {

	}

	public SpapDimRegion(int id, int countryId, String name) {
		this.id = id;
		this.countryId = countryId;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
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
			SpapDimRegion cloned = (SpapDimRegion) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimRegion(this.id, this.countryId, this.name);
		}
	}
}
