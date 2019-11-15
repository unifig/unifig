package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimIsp;

public interface SpapOdsDimIspService {

	public SpapDimIsp getIspByName(String ispName, boolean createIfNotExist);
	
	public SpapDimIsp findIspByName(String ispName);
	
	public SpapDimIsp getIspByIspId(int id);
	
	public SpapDimIsp findIspByIspId(int id);
	
	public void saveDimIsp(SpapDimIsp dimIsp);
}
