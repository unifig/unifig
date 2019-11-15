package etl.dispatch.java.ods.service;

import java.util.List;

import etl.dispatch.java.ods.domain.DimAppPlat;

public interface OdsDimAppPlatService{
	
	/**
	 * Guave查詢平台应用类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用类型
	 * @param name
	 * @return
	 */
	public DimAppPlat findAppPlatByName(String appPlatName);

	/**
	 * 保存平台应用类型
	 * @param newDimOs
	 */
	public void saveDimAppPlat(DimAppPlat newAppPlat);

	public List<DimAppPlat> getAllAppPlat();
}
