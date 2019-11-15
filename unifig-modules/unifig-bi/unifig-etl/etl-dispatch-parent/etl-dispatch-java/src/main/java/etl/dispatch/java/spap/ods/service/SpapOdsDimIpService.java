package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimIp;

public interface SpapOdsDimIpService {

	public SpapDimIp getDimIpByDimIp(SpapDimIp inDimIp, boolean createIfNotExist);

	public SpapDimIp getDimIpByIp(String ip, long ipNum);
	
	public SpapDimIp findDimIpByIp(String ip, long ipNum);

	public void saveDimIp(SpapDimIp dimIp);
}
