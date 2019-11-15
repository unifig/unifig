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
import etl.dispatch.java.ods.dao.DimOsDao;
import etl.dispatch.java.ods.domain.DimOs;
import etl.dispatch.java.ods.service.OdsDimOsService;
import etl.dispatch.util.StringUtil;

@Service
public class OdsDimOsServiceImpl implements OdsDimOsService, GuavaCacheScript {
	private static Logger logger = LoggerFactory.getLogger(OdsDimOsServiceImpl.class);
	// 终端操作系统维表字典转换; dim_os
	private LoadingCache<DimOsKey, DimOs> dimOsKeyCache;
	private final static DimOs OS_NOT_PRESENT = new DimOs();
	@Autowired
	private DimDataSource dimDataSource;
	@Autowired
	private DimOsDao dimOsDao;
	
	public OdsDimOsServiceImpl() {
		//终端操作系统Guave緩存
		this.dimOsKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimOsKeyCacheLoader(this));
	}

	/**
	 * Guave查詢终端操作系统
	 */
	@Override
	public DimOs getOsByName(String osName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(osName)) {
			return null;
		}
		if (osName.length() > DEFAULT_NAME_MAX_LENGTH) {
			osName = osName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		DimOs dimOs = null;
		try {
			DimOsKey dimOsKey = new DimOsKey(osName);
			if (createIfNotExist) {
				dimOs = this.dimOsKeyCache.get(dimOsKey);
			} else {
				dimOs = this.dimOsKeyCache.getIfPresent(dimOsKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimOs from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimOs from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimOs from cache: " + e.getMessage(), e);
		}
		return dimOs == null ? OS_NOT_PRESENT : dimOs;
	}

	/**
	 * 數據庫查詢终端操作系统
	 */
	@Override
	public DimOs findOsByName(String osName) {
		return this.dimOsDao.findOsByName(dimDataSource, osName);
	}

	/**
	 * 保存终端操作系统
	 */
	@Override
	public void saveDimOs(DimOs newOs) {
		this.dimOsDao.saveDimOs(dimDataSource, newOs);
	}
	
	/**
	 * Guave操作系统维表Key
	 *
	 */
	private static class DimOsKey {
		private String name;
		private String nameLowercase;

		public DimOsKey(String name) {
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
			DimOsKey other = (DimOsKey) obj;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}
	
	/**
	 * 终端操作系统Guave
	 *
	 */
	private static class DimOsKeyCacheLoader extends CacheLoader<DimOsKey, DimOs> {
		private OdsDimOsService odsDimOsService;
		private Lock lock;

		public DimOsKeyCacheLoader(OdsDimOsService odsDimOsService) {
			this.odsDimOsService = odsDimOsService;
			this.lock = new ReentrantLock();
		}

		@Override
		public DimOs load(DimOsKey dimOsKey) throws Exception {
			DimOs dimOs = odsDimOsService.findOsByName(dimOsKey.name);
			if (dimOs == null) {
				this.lock.lock();
				try {
					dimOs = this.odsDimOsService.findOsByName(dimOsKey.name);
					if (dimOs == null) {
						this.odsDimOsService.saveDimOs(new DimOs((short) 0, dimOsKey.name));
						dimOs = this.odsDimOsService.findOsByName(dimOsKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimOs == null ? OS_NOT_PRESENT : (DimOs) dimOs.clone();
		}
	}


}
