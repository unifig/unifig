package etl.dispatch.java.spap.ods.service;

import java.net.InetAddress;
import java.util.List;

import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;
import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;
import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;
import etl.dispatch.java.spap.ods.domain.SpapDimIp;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturer;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturerModel;
import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;
import etl.dispatch.java.spap.ods.domain.SpapDimOs;
import etl.dispatch.java.spap.ods.domain.SpapDimOsVersion;

public interface SpapOdsFullDimHolderService {

	/**
	 * Guave查詢平台应用类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist);

	/**
	 * 數據庫查詢平台应用类型
	 * @param name
	 * @return
	 */
	public SpapDimAppPlat findAppPlatByName(String appPlatName);

	/**
	 * 保存平台应用类型
	 * @param newDimOs
	 */
	public void saveDimAppPlat(SpapDimAppPlat newAppPlat);
	
	/**
	 * Guave查詢平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist);
	 
	/**
	 * Guave根据appId查詢平台应用版本
	 * @author: ylc
	 */
	public List<SpapDimAppVersion> getAppVersionById(int appPlatId,int appId);

	/**
	 * 数据库查询平台应用版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public SpapDimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName);

	/**
	 * 保存平台应用版本
	 * @param osVersion
	 */
	public void saveDimAppVersion(SpapDimAppVersion newAppVersion);
	
	/**
	 * Guave查詢工作行业
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimIndustry getIndustryByName(String industryName, boolean createIfNotExist);

	/**
	 * 數據庫查詢工作行业
	 * @param name
	 * @return
	 */
	public SpapDimIndustry findIndustryByName(String industryName);

	/**
	 * 保存工作行业
	 * @param newDimOs
	 */
	public void saveDimIndustry(SpapDimIndustry newIndustry);
	
	/**
	 * Guave查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端设备型号
	 * @param manufacturerId
	 * @param manufacturerModelName
	 * @return
	 */
	public SpapDimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName);

	/**
	 * 保存終端设备型号
	 * @param newManufacturerModel
	 */
	public void saveDimManufacturerModel(SpapDimManufacturerModel newManufacturerModel);
	
	/**
	 * Guave查詢終端平台厂商
	 * @param manufacturerName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist);

	/**
	 * 數據庫查詢終端平台厂商
	 * @param name
	 * @return
	 */
	public SpapDimManufacturer findManufacturerByName(String manufacturerName);

	/**
	 * 保存終端平台廠商
	 * @param manufacturer
	 */
	public void saveDimManufacturer(SpapDimManufacturer newManufacturer);
	
	/**
	 * Guave查詢终端操作系统
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimOs getOsByName(String osName, boolean createIfNotExist);

	/**
	 * 數據庫查詢终端操作系统
	 * @param name
	 * @return
	 */
	public SpapDimOs findOsByName(String osName);

	/**
	 * 保存终端操作系统
	 * @param newDimOs
	 */
	public void saveDimOs(SpapDimOs newOs);
	
	/**
	 * Guave查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist);

	/**
	 * 數據庫查詢操作系统版本
	 * @param osId
	 * @param osVersionName
	 * @return
	 */
	public SpapDimOsVersion findOsVersionByName(short osId, String osVersionName);

	/**
	 * 保存操作系统版本
	 * @param osVersion
	 */
	public void saveDimOsVersion(SpapDimOsVersion newOsVersion);
	
	/**
	 * Guave查詢网络类型
	 * @param osName
	 * @param createIfNotExist
	 * @return
	 */
	public SpapDimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist);

	/**
	 * 數據庫查詢网络类型
	 * @param name
	 * @return
	 */
	public SpapDimNetWork findNetWorkByName(String netWorkName);

	/**
	 * 保存网络类型
	 * @param newDimOs
	 */
	public void saveDimNetWork(SpapDimNetWork newNetWork);
	
	/**
	 * Ip地址库解析
	 * @param ipAddress
	 * @return
	 */
	public SpapDimIp getInformation(String ipAddress);
	
	/**
	 * Ip地址库解析(InetAddress)
	 * @param addr
	 * @return
	 */
	public SpapDimIp getInformation(InetAddress addr);
	
}
