package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimRegion;

public interface OdsDimRegionService {

	public DimRegion getDimRegionByName(int countryId, String regionName, boolean createIfNotExist);

	public DimRegion findDimRegionByName(int countryId, String regionName);
	
	public DimRegion getDimRegionByRegionId(int id, int countryId);
	
	public DimRegion findDimRegionByRegionId(int id, int countryId);

	public void saveDimRegion(DimRegion dimRegion);
}
