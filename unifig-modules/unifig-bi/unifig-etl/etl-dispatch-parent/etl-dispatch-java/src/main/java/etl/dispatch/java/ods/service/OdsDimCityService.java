package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimCity;

public interface OdsDimCityService {

	public DimCity getCityByName(int countryId, int regionId, String cityName, boolean createIfNotExist);
	
	public DimCity findCityByName(int countryId, int regionId, String cityName);
	
	public DimCity getCityByCityId(int id, int countryId, int regionId);
	
	public DimCity findCityByCityId(int id, int countryId, int regionId);
	
	public void saveDimCity(DimCity dimCity);
}
