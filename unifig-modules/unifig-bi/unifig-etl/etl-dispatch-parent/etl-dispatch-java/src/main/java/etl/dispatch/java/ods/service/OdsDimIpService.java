package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimIp;

public interface OdsDimIpService {

	public DimIp getDimIpByDimIp(DimIp inDimIp, boolean createIfNotExist);

	public DimIp getDimIpByIp(String ip, long ipNum);
	
	public DimIp findDimIpByIp(String ip, long ipNum);

	public void saveDimIp(DimIp dimIp);
}
