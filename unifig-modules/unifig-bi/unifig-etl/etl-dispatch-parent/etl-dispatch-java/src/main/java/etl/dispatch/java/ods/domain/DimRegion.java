package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 省份/地区
 * 
 *
 *
 */
public class DimRegion implements Serializable {
	private static final long serialVersionUID = 1640734798246539965L;

	private int id;

	private int countryId;

	private String name;

	public DimRegion() {

	}

	public DimRegion(int id, int countryId, String name) {
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
			DimRegion cloned = (DimRegion) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimRegion(this.id, this.countryId, this.name);
		}
	}
}
