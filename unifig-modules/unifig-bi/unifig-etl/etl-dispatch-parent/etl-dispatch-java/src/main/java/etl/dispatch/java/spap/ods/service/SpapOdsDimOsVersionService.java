package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimOsVersion;

public interface SpapOdsDimOsVersionService {

	/**
	 * Guave查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public SpapDimOsVersion findOsVersionByName(short osId, String osVersionName);

	/**
	 * 保存操作系统版本
	 * @param osVersion
	 */
	public void saveDimOsVersion(SpapDimOsVersion newOsVersion);
}
