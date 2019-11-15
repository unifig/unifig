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
import etl.dispatch.java.spap.ods.dao.SpapDimOsVersionDao;
import etl.dispatch.java.spap.ods.domain.SpapDimOsVersion;
import etl.dispatch.java.spap.ods.service.SpapOdsDimOsVersionService;
import etl.dispatch.util.StringUtil;

@Service
public class SpapOdsDimOsVersionServiceImpl implements SpapOdsDimOsVersionService, GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimOsVersionServiceImpl.class);
	// 操作系統版本维表字典转换; dim_os_version
	private LoadingCache<DimOsVersionKey, SpapDimOsVersion> dimOsVersionKeyCache;
	private final static SpapDimOsVersion OSVERSION_NOT_PRESENT = new SpapDimOsVersion();
	@Autowired
	private SpapDimDataSource dimDataSource;
	@Autowired
	private SpapDimOsVersionDao dimOsVersionDao;
	
	public SpapOdsDimOsVersionServiceImpl() {
		//操作系統版本Guave緩存
		this.dimOsVersionKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimOsVersionKeyCacheLoader(this));
	}

	/**
	 * Guave查詢操作系统版本
	 */
	@Override
	public SpapDimOsVersion getOsVersionByName(short osId, String osVersionName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(osVersionName)) {
			return null;
		}
		if (osVersionName.length() > DEFAULT_NAME_MAX_LENGTH) {
			osVersionName = osVersionName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimOsVersion dimOsVersion = null;
		try {
			DimOsVersionKey dimManufacturerModelKey = new DimOsVersionKey(osId, osVersionName);
			if (createIfNotExist) {
				dimOsVersion = this.dimOsVersionKeyCache.get(dimManufacturerModelKey);
			} else {
				dimOsVersion = this.dimOsVersionKeyCache.getIfPresent(dimManufacturerModelKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimOsVersion from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimOsVersion from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimOsVersion from cache: " + e.getMessage(), e);
		}
		return dimOsVersion == null ? OSVERSION_NOT_PRESENT : dimOsVersion;
	}

	/**
	 * 數據庫查詢操作系统版本
	 */
	@Override
	public SpapDimOsVersion findOsVersionByName(short osId, String osVersionName) {
		return this.dimOsVersionDao.findOsVersionByName(dimDataSource, osId, osVersionName);
	}

	/**
	 * 保存操作系统版本
	 */
	@Override
	public void saveDimOsVersion(SpapDimOsVersion newOsVersion) {
		this.dimOsVersionDao.saveDimOsVersion(dimDataSource, newOsVersion);
	}
	
	/**
	 * Guave操作系统版本维表Key
	 *
	 */
	private static class DimOsVersionKey {
		private short osId;
		private String name;
		private String nameLowercase;
		private int hash;

		DimOsVersionKey(short osId, String name) {
			this.osId = osId;
			this.name = name;
			this.nameLowercase = (name == null ? null : name.toLowerCase());
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + osId;
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
			DimOsVersionKey other = (DimOsVersionKey) obj;
			if (osId != other.osId)
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
	 * 操作系統版本Guave
	 * 
	 *
	 */
	private static class DimOsVersionKeyCacheLoader extends CacheLoader<DimOsVersionKey, SpapDimOsVersion> {
		private SpapOdsDimOsVersionService odsDimOsVersionService;
		private Lock lock;

		public DimOsVersionKeyCacheLoader(SpapOdsDimOsVersionService odsDimOsVersionService) {
			this.odsDimOsVersionService = odsDimOsVersionService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimOsVersion load(DimOsVersionKey dimOsVersionKey) throws Exception {
			SpapDimOsVersion dimOsVersion = odsDimOsVersionService.findOsVersionByName(dimOsVersionKey.osId, dimOsVersionKey.name);
			if (dimOsVersion == null) {
				this.lock.lock();
				try {
					dimOsVersion = this.odsDimOsVersionService.findOsVersionByName(dimOsVersionKey.osId, dimOsVersionKey.name);
					if (dimOsVersion == null) {
						this.odsDimOsVersionService.saveDimOsVersion(new SpapDimOsVersion((short) 0, dimOsVersionKey.osId, dimOsVersionKey.name));
						dimOsVersion = this.odsDimOsVersionService.findOsVersionByName(dimOsVersionKey.osId, dimOsVersionKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimOsVersion == null ? OSVERSION_NOT_PRESENT : (SpapDimOsVersion) dimOsVersion.clone();
		}
	}

}
