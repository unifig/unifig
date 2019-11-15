package etl.dispatch.java.spap.ods.service.impl;

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

import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;
import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;
import etl.dispatch.java.spap.ods.domain.SpapDimCity;
import etl.dispatch.java.spap.ods.domain.SpapDimCountry;
import etl.dispatch.java.spap.ods.domain.SpapDimCountryCode;
import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;
import etl.dispatch.java.spap.ods.domain.SpapDimIp;
import etl.dispatch.java.spap.ods.domain.SpapDimIsp;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturer;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturerModel;
import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;
import etl.dispatch.java.spap.ods.domain.SpapDimOs;
import etl.dispatch.java.spap.ods.domain.SpapDimOsVersion;
import etl.dispatch.java.spap.ods.domain.SpapDimRegion;
import etl.dispatch.java.spap.ods.service.SpapOdsDimAppPlatService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimAppVersionService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimCityService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimCountryService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimIndustryService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimIpService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimIspService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimManufacturerModelService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimManufacturerService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimNetWorkService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimOsService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimOsVersionService;
import etl.dispatch.java.spap.ods.service.SpapOdsDimRegionService;
import etl.dispatch.java.spap.ods.service.SpapOdsFullDimHolderService;
import etl.dispatch.script.util.AddressBytesUtil;
import etl.dispatch.util.StringUtil;

/**
 * 維表操作持有類
 * 
 *
 */
@Service
public class SpapOdsFullDimHolderServiceImpl implements SpapOdsFullDimHolderService {
	private final static Logger logger = LoggerFactory.getLogger(SpapOdsFullDimHolderServiceImpl.class);
	
	@Autowired
	private SpapOdsDimAppPlatService odsDimAppPlatService;

	@Autowired
	private SpapOdsDimAppVersionService odsDimAppVersionService;

	@Autowired
	private SpapOdsDimIndustryService odsDimIndustryService;

	@Autowired
	private SpapOdsDimManufacturerModelService odsDimManufacturerModelService;

	@Autowired
	private SpapOdsDimManufacturerService odsDimManufacturerService;

	@Autowired
	private SpapOdsDimOsService odsDimOsService;

	@Autowired
	private SpapOdsDimOsVersionService odsDimOsVersionService;

	@Autowired
	private SpapOdsDimNetWorkService odsDimNetWorkService;
	
	@Autowired
	private SpapOdsDimIpService odsDimIpService;
	
	@Autowired
	private SpapOdsDimIspService odsDimIspService;
	
	@Autowired
	private SpapOdsDimCountryService odsDimCountryService;
	
	@Autowired
	private SpapOdsDimRegionService odsDimRegionService;
	
	@Autowired
	private SpapOdsDimCityService odsDimCityService;
	
	@Override
	public SpapDimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist) {
		return this.odsDimAppPlatService.getAppPlatByName(appPlatName, createIfNotExist);
	}

	@Override
	public SpapDimAppPlat findAppPlatByName(String appPlatName) {
		return this.odsDimAppPlatService.findAppPlatByName(appPlatName);
	}

	@Override
	public void saveDimAppPlat(SpapDimAppPlat newAppPlat) {
		this.odsDimAppPlatService.saveDimAppPlat(newAppPlat);
	}

	@Override
	public SpapDimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist) {
		return this.odsDimAppVersionService.getAppVersionByName(appPlatId, appId, appVersionName, createIfNotExist);
	}

	@Override
	public SpapDimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName) {
		return this.odsDimAppVersionService.findAppVersionByName(appPlatId, appId, appVersionName);
	}
	
	@Override
	public List<SpapDimAppVersion> getAppVersionById(int appPlatId,int appId) {
		return this.odsDimAppVersionService.getAppVersionById(appPlatId,appId);
	}

	@Override
	public void saveDimAppVersion(SpapDimAppVersion newAppVersion) {
		this.odsDimAppVersionService.saveDimAppVersion(newAppVersion);

	}

	@Override
	public SpapDimIndustry getIndustryByName(String industryName, boolean createIfNotExist) {
		return this.odsDimIndustryService.getIndustryByName(industryName, createIfNotExist);
	}

	@Override
	public SpapDimIndustry findIndustryByName(String industryName) {
		return this.odsDimIndustryService.findIndustryByName(industryName);
	}

	@Override
	public void saveDimIndustry(SpapDimIndustry newIndustry) {
		this.odsDimIndustryService.saveDimIndustry(newIndustry);
	}

	@Override
	public SpapDimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist) {
		return this.odsDimManufacturerModelService.getManufacturerModelByName(manufacturerId, manufacturerModelName, createIfNotExist);
	}

	@Override
	public SpapDimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName) {
		return this.odsDimManufacturerModelService.findManufacturerModelByName(manufacturerId, manufacturerModelName);
	}

	@Override
	public void saveDimManufacturerModel(SpapDimManufacturerModel newManufacturerModel) {
		this.odsDimManufacturerModelService.saveDimManufacturerModel(newManufacturerModel);

	}

	@Override
	public SpapDimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist) {
		return this.odsDimManufacturerService.getManufacturerByName(manufacturerName, createIfNotExist);
	}

	@Override
	public SpapDimManufacturer findManufacturerByName(String manufacturerName) {
		return this.odsDimManufacturerService.findManufacturerByName(manufacturerName);
	}

	@Override
	public void saveDimManufacturer(SpapDimManufacturer newManufacturer) {
		this.odsDimManufacturerService.saveDimManufacturer(newManufacturer);
	}

	@Override
	public SpapDimOs getOsByName(String osName, boolean createIfNotExist) {
		return this.odsDimOsService.getOsByName(osName, createIfNotExist);
	}

	@Override
	public SpapDimOs findOsByName(String osName) {
		return this.odsDimOsService.findOsByName(osName);
	}

	@Override
	public void saveDimOs(SpapDimOs newOs) {
		this.odsDimOsService.saveDimOs(newOs);
	}

	@Override
	public SpapDimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist) {
		return this.odsDimOsVersionService.getOsVersionByName(osId, osVersionName, createIfNotExist);
	}

	@Override
	public SpapDimOsVersion findOsVersionByName(short osId, String osVersionName) {
		return this.odsDimOsVersionService.findOsVersionByName(osId, osVersionName);
	}

	@Override
	public void saveDimOsVersion(SpapDimOsVersion newOsVersion) {
		this.odsDimOsVersionService.saveDimOsVersion(newOsVersion);
	}

	@Override
	public SpapDimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist) {
		return this.odsDimNetWorkService.getNetWorkByName(netWorkName, createIfNotExist);
	}

	@Override
	public SpapDimNetWork findNetWorkByName(String netWorkName) {
		return this.odsDimNetWorkService.findNetWorkByName(netWorkName);
	}

	@Override
	public void saveDimNetWork(SpapDimNetWork newNetWork) {
		this.odsDimNetWorkService.saveDimNetWork(newNetWork);
	}

	@Override
	public SpapDimIp getInformation(String ipAddress) {
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
	public SpapDimIp getInformation(InetAddress addr) {
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
			SpapDimIsp dimIsp = this.odsDimIspService.getIspByName(ispName, true);
			if (null != dimIsp) {
				ispId = dimIsp.getId();
			}
			// 国家代码
			String countryCode = null;
			SpapDimCountryCode countryInfo = this.odsDimCountryService.getCountryCodeByName(countryName);
			if (null != countryInfo) {
				countryCode = countryInfo.getCode2();
			}
			if(StringUtil.isNullOrEmpty(countryCode)){
				countryCode = "N/A";
			}
			SpapDimCountry dimCountry = this.odsDimCountryService.getCountryByName(countryCode, countryName, true);
			if (null != dimCountry) {
				countryId = dimCountry.getId();
			}
			// 省份代码
			SpapDimRegion dimRegion = this.odsDimRegionService.getDimRegionByName(countryId, regionName, true);
			if (null != dimRegion) {
				regionId = dimRegion.getId();
			}
			// 地市代码
			SpapDimCity dimCity = this.odsDimCityService.getCityByName(countryId, regionId, cityName, true);
			if (null != dimCity) {
				cityId = dimCity.getId();
			}
			// Ip地址
			return this.odsDimIpService.getDimIpByDimIp(new SpapDimIp(ipAddress, AddressBytesUtil.bytesToLong(addr.getAddress()), countryId, regionId, cityId, ispId), true);
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
