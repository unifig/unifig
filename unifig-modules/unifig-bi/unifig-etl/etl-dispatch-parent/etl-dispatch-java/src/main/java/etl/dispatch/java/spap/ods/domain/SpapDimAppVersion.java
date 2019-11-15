package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 应用版本(平台类型下各种应用的版本)
 * 
 *
 */
public class SpapDimAppVersion implements Serializable {
	private static final long serialVersionUID = -7570774350002126014L;
	private int id;
	private int appPlatId;
	private int appId;
	private String appVersion;

	public SpapDimAppVersion() {

	}

	public SpapDimAppVersion(int id, int appPlatId, int appId, String appVersion) {
		this.id = id;
		this.appPlatId =appPlatId;
		this.appId = appId;
		this.appVersion = appVersion;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAppPlatId() {
		return appPlatId;
	}

	public void setAppPlatId(int appPlatId) {
		this.appPlatId = appPlatId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
	public Object clone() {
		try {
			SpapDimAppVersion cloned = (SpapDimAppVersion) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimAppVersion(this.id, this.appPlatId, this.appId, this.appVersion);
		}
	}
}
