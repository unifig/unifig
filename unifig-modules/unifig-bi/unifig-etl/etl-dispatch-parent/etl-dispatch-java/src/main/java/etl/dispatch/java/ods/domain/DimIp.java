package etl.dispatch.java.ods.domain;

import java.io.Serializable;

public class DimIp implements Serializable {
	private static final long serialVersionUID = -2576772118722160066L;
	private int id;

	private String ip;

	private long ipNum;

	private int countryId;

	private int regionId;

	private int cityId;

	private int ispId;

	public DimIp() {

	}

	public DimIp(String ip, long ipNum, int countryId, int regionId, int cityId, int ispId) {
		this.ip = ip;
		this.ipNum = ipNum;
		this.countryId = countryId;
		this.regionId = regionId;
		this.cityId = cityId;
		this.ispId = ispId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getIpNum() {
		return ipNum;
	}

	public void setIpNum(long ipNum) {
		this.ipNum = ipNum;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getIspId() {
		return ispId;
	}

	public void setIspId(int ispId) {
		this.ispId = ispId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			DimIp cloned = (DimIp) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimIp(this.ip, this.ipNum, this.countryId, this.regionId, this.cityId, this.ispId);
		}
	}
}
