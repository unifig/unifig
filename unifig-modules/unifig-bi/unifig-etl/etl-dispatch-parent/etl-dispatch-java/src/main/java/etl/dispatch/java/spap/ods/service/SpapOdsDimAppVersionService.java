package etl.dispatch.java.spap.ods.service;

import java.util.List;

import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;


public interface SpapOdsDimAppVersionService{

	/**
	 * Guave查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public SpapDimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName);

	/**
	 * 保存平台应用版本
	 * @param osVersion
	 */
	public void saveDimAppVersion(SpapDimAppVersion newAppVersion);

	public List<SpapDimAppVersion> getAppVersionById(int appPlatId, int appId);
	
}
