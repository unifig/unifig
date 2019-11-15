package etl.dispatch.java.spap.ods.service;

import etl.dispatch.java.spap.ods.domain.SpapDimCountry;
import etl.dispatch.java.spap.ods.domain.SpapDimCountryCode;

public interface SpapOdsDimCountryService {
	
	public SpapDimCountry getCountryByName(String countryCode , String countryName, boolean createIfNotExist);

	public SpapDimCountry findCountryByName(String countryCode,  String countryName);
	
	public SpapDimCountry getCountryByCountryId(int id);
	
	public SpapDimCountry findCountryByCountryId(int id);

	public void saveDimCountry(SpapDimCountry dimCountry);
	
	public SpapDimCountryCode getCountryCodeByName(String countryName);
	
}
