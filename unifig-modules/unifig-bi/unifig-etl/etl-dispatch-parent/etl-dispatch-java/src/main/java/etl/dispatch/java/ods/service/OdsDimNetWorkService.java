package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimNetWork;

public interface OdsDimNetWorkService {

	/**
	 * Guave查詢网络类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist);

	/**
	 * 數據庫查詢网络类型
	 * @param name
	 * @return
	 */
	public DimNetWork findNetWorkByName(String netWorkName);

	/**
	 * 保存网络类型
	 * @param newDimOs
	 */
	public void saveDimNetWork(DimNetWork newNetWork);

}
