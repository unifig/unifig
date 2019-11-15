package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimManufacturerModel;

public interface SpapOdsDimManufacturerModelService {

	/**
	 * Guave查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @return
	 */
	public SpapDimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName);

	/**
	 * 保存終端设备型号
	 * @param newManufacturerModel
	 */
	public void saveDimManufacturerModel(SpapDimManufacturerModel newManufacturerModel);

}
