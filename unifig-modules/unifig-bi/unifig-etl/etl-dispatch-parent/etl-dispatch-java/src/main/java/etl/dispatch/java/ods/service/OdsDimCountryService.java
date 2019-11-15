package etl.dispatch.java.ods.service;

import etl.dispatch.java.ods.domain.DimCountry;
import etl.dispatch.java.ods.domain.DimCountryCode;

public interface OdsDimCountryService {
	
	public DimCountry getCountryByName(String countryCode , String countryName, boolean createIfNotExist);

	public DimCountry findCountryByName(String countryCode,  String countryName);
	
	public DimCountry getCountryByCountryId(int id);
	
	public DimCountry findCountryByCountryId(int id);

	public void saveDimCountry(DimCountry dimCountry);
	
	public DimCountryCode getCountryCodeByName(String countryName);
	
}
