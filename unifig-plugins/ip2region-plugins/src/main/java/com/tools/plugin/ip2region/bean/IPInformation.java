package com.tools.plugin.ip2region.bean;

public class IPInformation {
	private Location location;
	private ISP isp;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ISP getIsp() {
		return isp;
	}

	public void setIsp(ISP isp) {
		this.isp = isp;
	}

	public static class ISP {
		// 运营商CODE
		private int code;
		// 运营商名称
		private String name;

		public ISP() {
			
		}
		public ISP(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Location {
		// 主键
		private int id;
		// 国家编码
		private String countryCode;
		// 国家名称
		private String countryName;
		// 省份Id
		private int regionId;
		// 省份名称
		private String regionName;
		// 地市Id
		private int cityId;
		// 地市名称
		private String cityName;
		// 邮政编码
		private String postalCode;
		// 维度
		private float latitude;
		// 经度
		private float longitude;

		public Location() {

		}

		public Location(String countryCode, String countryName, int regionId, String regionName, int cityId, String cityName) {
			this.countryCode = countryCode;
			this.countryName = countryName;
			this.regionId = regionId;
			this.regionName = regionName;
			this.cityId = cityId;
			this.cityName = cityName;
		}

		public Location(String countryCode, String countryName, int regionId, String regionName, int cityId, String cityName, float latitude, float longitude) {
			this.countryCode = countryCode;
			this.countryName = countryName;
			this.regionId = regionId;
			this.regionName = regionName;
			this.cityId = cityId;
			this.cityName = cityName;

			this.latitude = latitude;
			this.longitude = longitude;
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

		public String getCountryName() {
			return countryName;
		}

		public void setCountryName(String countryName) {
			this.countryName = countryName;
		}

		public int getRegionId() {
			return regionId;
		}

		public void setRegionId(int regionId) {
			this.regionId = regionId;
		}

		public String getRegionName() {
			return regionName;
		}

		public void setRegionName(String regionName) {
			this.regionName = regionName;
		}

		public int getCityId() {
			return cityId;
		}

		public void setCityId(int cityId) {
			this.cityId = cityId;
		}

		public String getCityName() {
			return cityName;
		}

		public void setCityName(String cityName) {
			this.cityName = cityName;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

		public float getLatitude() {
			return latitude;
		}

		public void setLatitude(float latitude) {
			this.latitude = latitude;
		}

		public float getLongitude() {
			return longitude;
		}

		public void setLongitude(float longitude) {
			this.longitude = longitude;
		}
	}

}
