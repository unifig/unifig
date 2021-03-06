package etl.dispatch.java.ods.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.GuavaCacheScript;
import etl.dispatch.java.ods.dao.DimManufacturerModelDao;
import etl.dispatch.java.ods.domain.DimManufacturerModel;
import etl.dispatch.java.ods.service.OdsDimManufacturerModelService;
import etl.dispatch.util.StringUtil;

@Service
public class OdsDimManufacturerModelServiceImpl implements OdsDimManufacturerModelService, GuavaCacheScript {
	private static Logger logger = LoggerFactory.getLogger(OdsDimManufacturerModelServiceImpl.class);
	// 终端设备型号维表字典转换; dim_manufacturer_model
	private LoadingCache<DimManufacturerModelKey, DimManufacturerModel> dimManufacturerModelKeyCache;
	private final static DimManufacturerModel MANUFACTURERMODEL_NOT_PRESENT = new DimManufacturerModel();
	
	@Autowired
	private DimDataSource dimDataSource;
	
	@Autowired
	private DimManufacturerModelDao dimManufacturerModelDao;
	
	public OdsDimManufacturerModelServiceImpl() {
		//终端设备型号Guave緩存
		this.dimManufacturerModelKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimManufacturerModelKeyCacheLoader(this));
	}

	/**
	 * Guave查詢終端设备型号
	 */
	@Override
	public DimManufacturerModel getManufacturerModelByName(int manufacturerId, String manufacturerModelName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(manufacturerModelName)) {
			return null;
		}
		if (manufacturerModelName.length() > DEFAULT_NAME_MAX_LENGTH) {
			manufacturerModelName = manufacturerModelName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		DimManufacturerModel dimManufacturerModel = null;
		try {
			DimManufacturerModelKey dimManufacturerModelKey = new DimManufacturerModelKey(manufacturerId, manufacturerModelName);
			if (createIfNotExist) {
				dimManufacturerModel = this.dimManufacturerModelKeyCache.get(dimManufacturerModelKey);
			} else {
				dimManufacturerModel = this.dimManufacturerModelKeyCache.getIfPresent(dimManufacturerModelKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimManufacturerModel from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimManufacturerModel from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimManufacturerModel from cache: " + e.getMessage(), e);
		}
		return dimManufacturerModel == null ? MANUFACTURERMODEL_NOT_PRESENT : dimManufacturerModel;
	}

	/**
	 * 數據庫查詢終端设备型号
	 */
	@Override
	public DimManufacturerModel findManufacturerModelByName(int manufacturerId, String manufacturerModelName) {
		return this.dimManufacturerModelDao.findManufacturerModelByName(dimDataSource, manufacturerId, manufacturerModelName);
	}

	/**
	 * 保存終端设备型号
	 */
	@Override
	public void saveDimManufacturerModel(DimManufacturerModel newManufacturerModel) {
		this.dimManufacturerModelDao.saveDimManufacturerModel(dimDataSource, newManufacturerModel);
	}
	
	/**
	 * Guave终端型号维表Key
	 *
	 */
	private static class DimManufacturerModelKey {
		private int manufactoruerId;
		private String name;
		private String nameLowercase;
		private int hash;

		DimManufacturerModelKey(int manufactoruerId, String name) {
			this.manufactoruerId = manufactoruerId;
			this.name = name;
			this.nameLowercase = (name == null ? null : name.toLowerCase());
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + manufactoruerId;
				result = prime * result + ((nameLowercase == null) ? 0 : nameLowercase.hashCode());
				hash = result;
			}

			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DimManufacturerModelKey other = (DimManufacturerModelKey) obj;
			if (manufactoruerId != other.manufactoruerId)
				return false;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}
	
	/**
	 * 终端设备型号
	 *
	 */
	private static class DimManufacturerModelKeyCacheLoader extends CacheLoader<DimManufacturerModelKey, DimManufacturerModel> {
		private OdsDimManufacturerModelService odsDimManufacturerModelService;
		private Lock lock;

		public DimManufacturerModelKeyCacheLoader(OdsDimManufacturerModelService odsDimManufacturerModelService) {
			this.odsDimManufacturerModelService = odsDimManufacturerModelService;
			this.lock = new ReentrantLock();
		}

		@Override
		public DimManufacturerModel load(DimManufacturerModelKey dimManufacturerModelKey) throws Exception {
			DimManufacturerModel dimManufacturerModel = odsDimManufacturerModelService.findManufacturerModelByName(dimManufacturerModelKey.manufactoruerId, dimManufacturerModelKey.name);
			if (dimManufacturerModel == null) {
				this.lock.lock();
				try {
					dimManufacturerModel = this.odsDimManufacturerModelService.findManufacturerModelByName(dimManufacturerModelKey.manufactoruerId, dimManufacturerModelKey.name);
					if (dimManufacturerModel == null) {
						this.odsDimManufacturerModelService.saveDimManufacturerModel(new DimManufacturerModel(0, dimManufacturerModelKey.manufactoruerId, dimManufacturerModelKey.name));
						dimManufacturerModel = this.odsDimManufacturerModelService.findManufacturerModelByName(dimManufacturerModelKey.manufactoruerId, dimManufacturerModelKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimManufacturerModel == null ? MANUFACTURERMODEL_NOT_PRESENT : (DimManufacturerModel) dimManufacturerModel.clone();
		}
	}

}
