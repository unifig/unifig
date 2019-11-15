package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimOs;

public interface SpapOdsDimOsService {

	/**
	 * Guave查詢终端操作系统
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimOs getOsByName(String osName, boolean createIfNotExist);

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public SpapDimOs findOsByName(String osName);

	/**
	 * 保存终端操作系统
	 * @param newDimOs
	 */
	public void saveDimOs(SpapDimOs newOs);

}
