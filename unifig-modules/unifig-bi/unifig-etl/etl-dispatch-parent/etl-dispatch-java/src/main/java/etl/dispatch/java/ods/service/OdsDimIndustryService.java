package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimIndustry;

public interface OdsDimIndustryService{
	
	/**
	 * Guave查詢工作行业
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimIndustry getIndustryByName(String industryName, boolean createIfNotExist);

	/**
	 * 數據庫查詢工作行业
	 * @param name
	 * @return
	 */
	public DimIndustry findIndustryByName(String industryName);

	/**
	 * 保存工作行业
	 * @param newDimOs
	 */
	public void saveDimIndustry(DimIndustry newIndustry);
}
