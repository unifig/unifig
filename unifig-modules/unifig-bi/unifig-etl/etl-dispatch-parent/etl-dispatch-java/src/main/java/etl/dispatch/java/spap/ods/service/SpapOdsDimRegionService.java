package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimRegion;

public interface SpapOdsDimRegionService {

	public SpapDimRegion getDimRegionByName(int countryId, String regionName, boolean createIfNotExist);

	public SpapDimRegion findDimRegionByName(int countryId, String regionName);
	
	public SpapDimRegion getDimRegionByRegionId(int id, int countryId);
	
	public SpapDimRegion findDimRegionByRegionId(int id, int countryId);

	public void saveDimRegion(SpapDimRegion dimRegion);
}
