package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimCity;

public interface SpapOdsDimCityService {

	public SpapDimCity getCityByName(int countryId, int regionId, String cityName, boolean createIfNotExist);
	
	public SpapDimCity findCityByName(int countryId, int regionId, String cityName);
	
	public SpapDimCity getCityByCityId(int id, int countryId, int regionId);
	
	public SpapDimCity findCityByCityId(int id, int countryId, int regionId);
	
	public void saveDimCity(SpapDimCity dimCity);
}
