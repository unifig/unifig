package etl.dispatch.java.ods.service;

import java.net.InetAddress;
import java.util.List;

import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.domain.DimIndustry;
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.java.ods.domain.DimManufacturer;
import etl.dispatch.java.ods.domain.DimManufacturerModel;
import etl.dispatch.java.ods.domain.DimNetWork;
import etl.dispatch.java.ods.domain.DimOs;
import etl.dispatch.java.ods.domain.DimOsVersion;

public interface OdsFullDimHolderService {

	/**
	 * Guave查詢平台应用类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用类型
	 * @param name
	 * @return
	 */
	public DimAppPlat findAppPlatByName(String appPlatName);

	/**
	 * 保存平台应用类型
	 * @param newDimOs
	 */
	public void saveDimAppPlat(DimAppPlat newAppPlat);
	
	/**
	 * Guave查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public DimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist);
	 
	/**
	 * Guave根据appId查詢平台应用版本
	 * @author: ylc
	 */
	public List<DimAppVersion> getAppVersionById(int appPlatId,int appId);

	/**
	 * 数据库查询平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public DimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName);

	/**
	 * 保存平台应用版本
	 * @param osVersion
	 */
	public void saveDimAppVersion(DimAppVersion newAppVersion);
	
	/**
	 * Guave查詢工作行业
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimIndustry getIndustryByName(String industryName, boolean createIfNotExist);

	/**
	 * 數據庫查詢工作行业
	 * @param name
	 * @return
	 */
	public DimIndustry findIndustryByName(String industryName);

	/**
	 * 保存工作行业
	 * @param newDimOs
	 */
	public void saveDimIndustry(DimIndustry newIndustry);
	
	/**
	 * Guave查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @param createIfNotExist
	 * @return
	 */
	public DimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @return
	 */
	public DimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName);

	/**
	 * 保存終端设备型号
	 * @param newManufacturerModel
	 */
	public void saveDimManufacturerModel(DimManufacturerModel newManufacturerModel);
	
	/**
	 * Guave查詢終端平台厂商
	 * @param manufacturerName
	 * @param createIfNotExist
	 * @return
	 */
	public DimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端平台厂商
	 * @param name
	 * @return
	 */
	public DimManufacturer findManufacturerByName(String manufacturerName);

	/**
	 * 保存終端平台廠商
	 * @param manufacturer
	 */
	public void saveDimManufacturer(DimManufacturer newManufacturer);
	
	/**
	 * Guave查詢终端操作系统
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimOs getOsByName(String osName, boolean createIfNotExist);

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public DimOs findOsByName(String osName);

	/**
	 * 保存终端操作系统
	 * @param newDimOs
	 */
	public void saveDimOs(DimOs newOs);
	
	/**
	 * Guave查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public DimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public DimOsVersion findOsVersionByName(short osId, String osVersionName);

	/**
	 * 保存操作系统版本
	 * @param osVersion
	 */
	public void saveDimOsVersion(DimOsVersion newOsVersion);
	
	/**
	 * Guave查詢网络类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public DimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist);

	/**
	 * 數據庫查詢网络类型
	 * @param name
	 * @return
	 */
	public DimNetWork findNetWorkByName(String netWorkName);

	/**
	 * 保存网络类型
	 * @param newDimOs
	 */
	public void saveDimNetWork(DimNetWork newNetWork);
	
	/**
	 * Ip地址库解析
	 * @param ipAddress
	 * @return
	 */
	public DimIp getInformation(String ipAddress);
	
	/**
	 * Ip地址库解析(InetAddress)
	 * @param addr
	 * @return
	 */
	public DimIp getInformation(InetAddress addr);
	
}
