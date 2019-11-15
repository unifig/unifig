package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;

public interface SpapOdsDimIndustryService{
	
	/**
	 * Guave查詢工作行业
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimIndustry getIndustryByName(String industryName, boolean createIfNotExist);

	/**
	 * 數據庫查詢工作行业
	 * @param name
	 * @return
	 */
	public SpapDimIndustry findIndustryByName(String industryName);

	/**
	 * 保存工作行业
	 * @param newDimOs
	 */
	public void saveDimIndustry(SpapDimIndustry newIndustry);
}
