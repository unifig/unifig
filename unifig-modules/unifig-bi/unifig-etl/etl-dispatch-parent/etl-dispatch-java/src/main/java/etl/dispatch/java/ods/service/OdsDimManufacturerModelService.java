package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimManufacturerModel;

public interface OdsDimManufacturerModelService {

	/**
	 * Guave查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @param createIfNotExist
	 * @return
	 */
	public DimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @return
	 */
	public DimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName);

	/**
	 * 保存終端设备型号
	 * @param newManufacturerModel
	 */
	public void saveDimManufacturerModel(DimManufacturerModel newManufacturerModel);

}
