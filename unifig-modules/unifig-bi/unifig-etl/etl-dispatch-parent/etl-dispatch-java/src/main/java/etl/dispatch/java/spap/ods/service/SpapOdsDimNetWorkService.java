package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;

public interface SpapOdsDimNetWorkService {

	/**
	 * Guave查詢网络类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist);

	/**
	 * 數據庫查詢网络类型
	 * @param name
	 * @return
	 */
	public SpapDimNetWork findNetWorkByName(String netWorkName);

	/**
	 * 保存网络类型
	 * @param newDimOs
	 */
	public void saveDimNetWork(SpapDimNetWork newNetWork);

}
