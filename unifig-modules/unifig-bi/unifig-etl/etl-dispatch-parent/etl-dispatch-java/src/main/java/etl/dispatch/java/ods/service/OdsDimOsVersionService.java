package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimOsVersion;

public interface OdsDimOsVersionService {

	/**
	 * Guave查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public DimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public DimOsVersion findOsVersionByName(short osId, String osVersionName);

	/**
	 * 保存操作系统版本
	 * @param osVersion
	 */
	public void saveDimOsVersion(DimOsVersion newOsVersion);
}
