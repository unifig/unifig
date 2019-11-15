package etl.dispatch.java.spap.ods.service;

import java.util.List;

import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;

public interface SpapOdsDimAppPlatService{
	
	/**
	 * Guave查詢平台应用类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用类型
	 * @param name
	 * @return
	 */
	public SpapDimAppPlat findAppPlatByName(String appPlatName);

	/**
	 * 保存平台应用类型
	 * @param newDimOs
	 */
	public void saveDimAppPlat(SpapDimAppPlat newAppPlat);

	public List<SpapDimAppPlat> getAllAppPlat();
}
