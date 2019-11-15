package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimIsp;

public interface OdsDimIspService {

	public DimIsp getIspByName(String ispName, boolean createIfNotExist);
	
	public DimIsp findIspByName(String ispName);
	
	public DimIsp getIspByIspId(int id);
	
	public DimIsp findIspByIspId(int id);
	
	public void saveDimIsp(DimIsp dimIsp);
}
