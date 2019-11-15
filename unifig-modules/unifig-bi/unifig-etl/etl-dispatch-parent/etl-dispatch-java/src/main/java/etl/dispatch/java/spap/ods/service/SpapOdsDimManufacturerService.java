package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimManufacturer;

public interface SpapOdsDimManufacturerService {

	/**
	 * Guave查詢終端平台厂商
	 * @param manufacturerName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端平台厂商
	 * @param name
	 * @return
	 */
	public SpapDimManufacturer findManufacturerByName(String manufacturerName);

	/**
	 * 保存終端平台廠商
	 * @param manufacturer
	 */
	public void saveDimManufacturer(SpapDimManufacturer newManufacturer);

}
