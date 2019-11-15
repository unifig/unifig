package etl.dispatch.java.ods.service;

import java.util.List;

import etl.dispatch.java.ods.domain.DimAppVersion;

public interface OdsDimAppVersionService{

	/**
	 * Guave查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public DimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public DimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName);

	/**
	 * 保存平台应用版本
	 * @param osVersion
	 */
	public void saveDimAppVersion(DimAppVersion newAppVersion);

	public List<DimAppVersion> getAppVersionById(int appPlatId, int appId);
	
}
