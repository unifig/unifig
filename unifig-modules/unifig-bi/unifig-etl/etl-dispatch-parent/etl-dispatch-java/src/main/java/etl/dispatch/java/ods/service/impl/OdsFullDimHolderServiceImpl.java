package etl.dispatch.java.ods.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tools.plugin.ip2region.IpSearcher;
import com.tools.plugin.ip2region.bean.IPInformation;

import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.domain.DimCity;
import etl.dispatch.java.ods.domain.DimCountry;
import etl.dispatch.java.ods.domain.DimCountryCode;
import etl.dispatch.java.ods.domain.DimIndustry;
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.java.ods.domain.DimIsp;
import etl.dispatch.java.ods.domain.DimManufacturer;
import etl.dispatch.java.ods.domain.DimManufacturerModel;
import etl.dispatch.java.ods.domain.DimNetWork;
import etl.dispatch.java.ods.domain.DimOs;
import etl.dispatch.java.ods.domain.DimOsVersion;
import etl.dispatch.java.ods.domain.DimRegion;
import etl.dispatch.java.ods.service.OdsDimAppPlatService;
import etl.dispatch.java.ods.service.OdsDimAppVersionService;
import etl.dispatch.java.ods.service.OdsDimCityService;
import etl.dispatch.java.ods.service.OdsDimCountryService;
import etl.dispatch.java.ods.service.OdsDimIndustryService;
import etl.dispatch.java.ods.service.OdsDimIpService;
import etl.dispatch.java.ods.service.OdsDimIspService;
import etl.dispatch.java.ods.service.OdsDimManufacturerModelService;
import etl.dispatch.java.ods.service.OdsDimManufacturerService;
import etl.dispatch.java.ods.service.OdsDimNetWorkService;
import etl.dispatch.java.ods.service.OdsDimOsService;
import etl.dispatch.java.ods.service.OdsDimOsVersionService;
import etl.dispatch.java.ods.service.OdsDimRegionService;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
import etl.dispatch.script.util.AddressBytesUtil;
import etl.dispatch.util.StringUtil;

/**
 * 維表操作持有類
 * 
 *
 */
@Service
public class OdsFullDimHolderServiceImpl implements OdsFullDimHolderService {
	private final static Logger logger = LoggerFactory.getLogger(OdsFullDimHolderServiceImpl.class);
	
	@Autowired
	private OdsDimAppPlatService odsDimAppPlatService;

	@Autowired
	private OdsDimAppVersionService odsDimAppVersionService;

	@Autowired
	private OdsDimIndustryService odsDimIndustryService;

	@Autowired
	private OdsDimManufacturerModelService odsDimManufacturerModelService;

	@Autowired
	private OdsDimManufacturerService odsDimManufacturerService;

	@Autowired
	private OdsDimOsService odsDimOsService;

	@Autowired
	private OdsDimOsVersionService odsDimOsVersionService;

	@Autowired
	private OdsDimNetWorkService odsDimNetWorkService;
	
	@Autowired
	private OdsDimIpService odsDimIpService;
	
	@Autowired
	private OdsDimIspService odsDimIspService;
	
	@Autowired
	private OdsDimCountryService odsDimCountryService;
	
	@Autowired
	private OdsDimRegionService odsDimRegionService;
	
	@Autowired
	private OdsDimCityService odsDimCityService;
	
	@Override
	public DimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist) {
		return this.odsDimAppPlatService.getAppPlatByName(appPlatName, createIfNotExist);
	}

	@Override
	public DimAppPlat findAppPlatByName(String appPlatName) {
		return this.odsDimAppPlatService.findAppPlatByName(appPlatName);
	}

	@Override
	public void saveDimAppPlat(DimAppPlat newAppPlat) {
		this.odsDimAppPlatService.saveDimAppPlat(newAppPlat);
	}

	@Override
	public DimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist) {
		return this.odsDimAppVersionService.getAppVersionByName(appPlatId, appId, appVersionName, createIfNotExist);
	}

	@Override
	public DimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName) {
		return this.odsDimAppVersionService.findAppVersionByName(appPlatId, appId, appVersionName);
	}
	
	@Override
	public List<DimAppVersion> getAppVersionById(int appPlatId,int appId) {
		return this.odsDimAppVersionService.getAppVersionById(appPlatId,appId);
	}

	@Override
	public void saveDimAppVersion(DimAppVersion newAppVersion) {
		this.odsDimAppVersionService.saveDimAppVersion(newAppVersion);

	}

	@Override
	public DimIndustry getIndustryByName(String industryName, boolean createIfNotExist) {
		return this.odsDimIndustryService.getIndustryByName(industryName, createIfNotExist);
	}

	@Override
	public DimIndustry findIndustryByName(String industryName) {
		return this.odsDimIndustryService.findIndustryByName(industryName);
	}

	@Override
	public void saveDimIndustry(DimIndustry newIndustry) {
		this.odsDimIndustryService.saveDimIndustry(newIndustry);
	}

	@Override
	public DimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist) {
		return this.odsDimManufacturerModelService.getManufacturerModelByName(manufacturerId, manufacturerModelName, createIfNotExist);
	}

	@Override
	public DimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName) {
		return this.odsDimManufacturerModelService.findManufacturerModelByName(manufacturerId, manufacturerModelName);
	}

	@Override
	public void saveDimManufacturerModel(DimManufacturerModel newManufacturerModel) {
		this.odsDimManufacturerModelService.saveDimManufacturerModel(newManufacturerModel);

	}

	@Override
	public DimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist) {
		return this.odsDimManufacturerService.getManufacturerByName(manufacturerName, createIfNotExist);
	}

	@Override
	public DimManufacturer findManufacturerByName(String manufacturerName) {
		return this.odsDimManufacturerService.findManufacturerByName(manufacturerName);
	}

	@Override
	public void saveDimManufacturer(DimManufacturer newManufacturer) {
		this.odsDimManufacturerService.saveDimManufacturer(newManufacturer);
	}

	@Override
	public DimOs getOsByName(String osName, boolean createIfNotExist) {
		return this.odsDimOsService.getOsByName(osName, createIfNotExist);
	}

	@Override
	public DimOs findOsByName(String osName) {
		return this.odsDimOsService.findOsByName(osName);
	}

	@Override
	public void saveDimOs(DimOs newOs) {
		this.odsDimOsService.saveDimOs(newOs);
	}

	@Override
	public DimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist) {
		return this.odsDimOsVersionService.getOsVersionByName(osId, osVersionName, createIfNotExist);
	}

	@Override
	public DimOsVersion findOsVersionByName(short osId, String osVersionName) {
		return this.odsDimOsVersionService.findOsVersionByName(osId, osVersionName);
	}

	@Override
	public void saveDimOsVersion(DimOsVersion newOsVersion) {
		this.odsDimOsVersionService.saveDimOsVersion(newOsVersion);
	}

	@Override
	public DimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist) {
		return this.odsDimNetWorkService.getNetWorkByName(netWorkName, createIfNotExist);
	}

	@Override
	public DimNetWork findNetWorkByName(String netWorkName) {
		return this.odsDimNetWorkService.findNetWorkByName(netWorkName);
	}

	@Override
	public void saveDimNetWork(DimNetWork newNetWork) {
		this.odsDimNetWorkService.saveDimNetWork(newNetWork);
	}

	@Override
	public DimIp getInformation(String ipAddress) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		return getInformation(addr);
	}

	@Override
	public DimIp getInformation(InetAddress addr) {
		try {
			String ipAddress = addr.getHostAddress();
			IPInformation ipInfo = IpSearcher.getIpInfo(ipAddress);
			if (null == ipInfo) {
				return null;
			}
			IPInformation.Location location = ipInfo.getLocation();
			IPInformation.ISP isp = ipInfo.getIsp();
			int countryId = -9;
			int regionId = -9;
			int cityId = -9;
			int ispId = -9;
			String countryName = ("N/A".equalsIgnoreCase(location.getCountryName().toUpperCase()) || "0".equalsIgnoreCase(location.getCountryName())) ? "未知" : location.getCountryName();
			String regionName = ("N/A".equalsIgnoreCase(location.getRegionName().toUpperCase()) || "0".equalsIgnoreCase(location.getRegionName())) ? "未知" : location.getRegionName();
			String cityName = ("N/A".equalsIgnoreCase(location.getCityName().toUpperCase()) || "0".equalsIgnoreCase(location.getCityName())) ? "未知" : location.getCityName();
			String ispName = ("N/A".equalsIgnoreCase(isp.getName().toUpperCase()) || "0".equalsIgnoreCase(isp.getName())) ? "未知" : isp.getName();
			// Isp运营商
			DimIsp dimIsp = this.odsDimIspService.getIspByName(ispName, true);
			if (null != dimIsp) {
				ispId = dimIsp.getId();
			}
			// 国家代码
			String countryCode = null;
			DimCountryCode countryInfo = this.odsDimCountryService.getCountryCodeByName(countryName);
			if (null != countryInfo) {
				countryCode = countryInfo.getCode2();
			}
			if(StringUtil.isNullOrEmpty(countryCode)){
				countryCode = "N/A";
			}
			DimCountry dimCountry = this.odsDimCountryService.getCountryByName(countryCode, countryName, true);
			if (null != dimCountry) {
				countryId = dimCountry.getId();
			}
			// 省份代码
			DimRegion dimRegion = this.odsDimRegionService.getDimRegionByName(countryId, regionName, true);
			if (null != dimRegion) {
				regionId = dimRegion.getId();
			}
			// 地市代码
			DimCity dimCity = this.odsDimCityService.getCityByName(countryId, regionId, cityName, true);
			if (null != dimCity) {
				cityId = dimCity.getId();
			}
			// Ip地址
			return this.odsDimIpService.getDimIpByDimIp(new DimIp(ipAddress, AddressBytesUtil.bytesToLong(addr.getAddress()), countryId, regionId, cityId, ispId), true);
		} catch (NoSuchMethodException ex) {
			logger.error("find ip by ip2region plugin NoSuchMethodException error ", ex.getMessage());
		} catch (IllegalAccessException ex) {
			logger.error("find ip by ip2region plugin IllegalAccessException error ", ex.getMessage());
		} catch (InvocationTargetException ex) {
			logger.error("find ip by ip2region plugin InvocationTargetException error ", ex.getMessage());
		} catch (IOException ex) {
			logger.error("find ip by ip2region plugin IOExceptionerror ", ex.getMessage());
		}
		return null;
	}

}
