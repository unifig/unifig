package etl.dispatch.java.spap.ods.service.impl;

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
import etl.dispatch.base.datasource.SpapDimDataSource;
import etl.dispatch.java.spap.ods.GuavaCacheSpap;
import etl.dispatch.java.spap.ods.dao.SpapDimManufacturerDao;
import etl.dispatch.java.spap.ods.domain.SpapDimManufacturer;
import etl.dispatch.java.spap.ods.service.SpapOdsDimManufacturerService;
import etl.dispatch.util.StringUtil;

@Service
public class SpapOdsDimManufacturerServiceImpl implements SpapOdsDimManufacturerService, GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimManufacturerServiceImpl.class);
	// 终端平台厂商维表字典转换; dim_manufacturer
	private LoadingCache<DimManufacturerKey, SpapDimManufacturer> dimManufacturerKeyCache;
	private final static SpapDimManufacturer MANUFACTURER_NOT_PRESENT = new SpapDimManufacturer();
	@Autowired
	private SpapDimDataSource dimDataSource;
	@Autowired
	private SpapDimManufacturerDao dimManufacturerDao;
	
	public SpapOdsDimManufacturerServiceImpl() {
		//终端平台厂商Guave緩存
		this.dimManufacturerKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimManufacturerKeyCacheLoader(this));
	}

	/**
	 * Guave查詢終端平台厂商
	 */
	@Override
	public SpapDimManufacturer getManufacturerByName(String manufacturerName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(manufacturerName)) {
			return null;
		}
		if (manufacturerName.length() > DEFAULT_NAME_MAX_LENGTH) {
			manufacturerName = manufacturerName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimManufacturer dimManufacturer = null;
		try {
			DimManufacturerKey dimManufacturerKey = new DimManufacturerKey(manufacturerName);
			if (createIfNotExist) {
				dimManufacturer = this.dimManufacturerKeyCache.get(dimManufacturerKey);
			} else {
				dimManufacturer = this.dimManufacturerKeyCache.getIfPresent(dimManufacturerKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimManufacturer from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimManufacturer from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimManufacturer from cache: " + e.getMessage(), e);
		}
		return dimManufacturer == null ? MANUFACTURER_NOT_PRESENT : dimManufacturer;
	}

	/**
	 * 數據庫查詢終端平台厂商
	 */
	@Override
	public SpapDimManufacturer findManufacturerByName(String manufacturerName) {
		return this.dimManufacturerDao.findManufacturerByName(dimDataSource, manufacturerName);
	}

	/**
	 * 保存終端平台廠商
	 */
	@Override
	public void saveDimManufacturer(SpapDimManufacturer newManufacturer) {
		this.dimManufacturerDao.saveDimManufacturer(dimDataSource, newManufacturer);
	}
	
	/**
	 * Guave终端厂商维表Key
	 *
	 */
	private static class DimManufacturerKey {
		private String name;
		private String nameLowercase;

		public DimManufacturerKey(String name) {
			this.name = name;
			this.nameLowercase = (name == null ? null : name.toLowerCase());
		}

		@Override
		public int hashCode() {
			return this.nameLowercase == null ? null : this.nameLowercase.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DimManufacturerKey other = (DimManufacturerKey) obj;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}
	
	/**
	 * 终端平台厂商Guave
	 *
	 */
	private static class DimManufacturerKeyCacheLoader extends CacheLoader<DimManufacturerKey, SpapDimManufacturer> {
		private SpapOdsDimManufacturerService odsDimManufacturerService;
		private Lock lock;

		public DimManufacturerKeyCacheLoader(SpapOdsDimManufacturerService odsDimManufacturerService) {
			this.odsDimManufacturerService = odsDimManufacturerService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimManufacturer load(DimManufacturerKey dimManufacturerKey) throws Exception {
			SpapDimManufacturer dimManufacturer = odsDimManufacturerService.findManufacturerByName(dimManufacturerKey.name);
			if (dimManufacturer == null) {
				this.lock.lock();
				try {
					dimManufacturer = this.odsDimManufacturerService.findManufacturerByName(dimManufacturerKey.name);
					if (dimManufacturer == null) {
						this.odsDimManufacturerService.saveDimManufacturer(new SpapDimManufacturer(0, dimManufacturerKey.name));
						dimManufacturer = this.odsDimManufacturerService.findManufacturerByName(dimManufacturerKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimManufacturer == null ? MANUFACTURER_NOT_PRESENT : (SpapDimManufacturer) dimManufacturer.clone();
		}
	}
}
