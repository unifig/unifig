package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimManufacturer;

public interface OdsDimManufacturerService {

	/**
	 * Guave查詢終端平台厂商
	 * @param manufacturerName
	 * @param createIfNotExist
	 * @return
	 */
	public DimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端平台厂商
	 * @param name
	 * @return
	 */
	public DimManufacturer findManufacturerByName(String manufacturerName);

	/**
	 * 保存終端平台廠商
	 * @param manufacturer
	 */
	public void saveDimManufacturer(DimManufacturer newManufacturer);

}
