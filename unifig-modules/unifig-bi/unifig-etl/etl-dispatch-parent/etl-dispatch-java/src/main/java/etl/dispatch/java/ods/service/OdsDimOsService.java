package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimOs;

public interface OdsDimOsService {

	/**
	 * Guave查詢终端操作系统
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimOs getOsByName(String osName, boolean createIfNotExist);

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public DimOs findOsByName(String osName);

	/**
	 * 保存终端操作系统
	 * @param newDimOs
	 */
	public void saveDimOs(DimOs newOs);

}
