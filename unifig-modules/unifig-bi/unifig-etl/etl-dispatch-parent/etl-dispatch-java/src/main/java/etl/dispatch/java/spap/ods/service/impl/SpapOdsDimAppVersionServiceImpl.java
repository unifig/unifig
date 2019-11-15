package etl.dispatch.java.spap.ods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.datasource.SpapDimDataSource;
import etl.dispatch.java.spap.ods.GuavaCacheSpap;
import etl.dispatch.java.spap.ods.dao.SpapDimAppVersionDao;
import etl.dispatch.java.spap.ods.domain.SpapDimAppVersion;
import etl.dispatch.java.spap.ods.service.SpapOdsDimAppVersionService;
import etl.dispatch.util.StringUtil;

@Service
public class SpapOdsDimAppVersionServiceImpl implements SpapOdsDimAppVersionService, GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimAppVersionServiceImpl.class);
	private LoadingCache<DimAppVersionKey, SpapDimAppVersion> dimAppVersionKeyCache;
	private final static SpapDimAppVersion APPVERSION_NOT_PRESENT = new SpapDimAppVersion();

	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimAppVersionDao dimAppVersionDao;
	
	public SpapOdsDimAppVersionServiceImpl() {
		//平台应用版本Guave緩存
		this.dimAppVersionKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimAppVersionKeyCacheLoader(this));
	}

	/**
	 * Guave查詢平台应用版本
	 */
	@Override
	public SpapDimAppVersion getAppVersionByName(int appPlatId, int appId, String appVersionName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(appVersionName)) {
			return null;
		}
		if (appVersionName.length() > DEFAULT_NAME_MAX_LENGTH) {
			appVersionName = appVersionName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimAppVersion dimAppVersion = null;
		try {
			DimAppVersionKey dimAppVersionKey = new DimAppVersionKey(appPlatId, appId, appVersionName);
			if (createIfNotExist) {
				dimAppVersion = this.dimAppVersionKeyCache.get(dimAppVersionKey);
			} else {
				dimAppVersion = this.dimAppVersionKeyCache.getIfPresent(dimAppVersionKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimAppVersion from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimAppVersion from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimAppVersion from cache: " + e.getMessage(), e);
		}
		return dimAppVersion == null ? APPVERSION_NOT_PRESENT : dimAppVersion;
	}
	
	@Override
	public List<SpapDimAppVersion> getAppVersionById(int appPlatId, int appId) {
		List<SpapDimAppVersion> versions = new ArrayList<>();
		ImmutableMap<DimAppVersionKey, SpapDimAppVersion> all = this.dimAppVersionKeyCache.getAllPresent(versions);
		if (all.isEmpty()) {
			versions = this.dimAppVersionDao.findAppVersionById(dimDataSource, appPlatId, appId);
		}else{
			versions = all.values().stream().filter(t -> {
				boolean flag = false;
				if (t.getAppId() == appId && t.getAppPlatId() == appPlatId) {
					flag = true;
				}
				return flag;
			}).collect(Collectors.toList());
		}
		return versions;
	}

	/**
	 * 數據庫查詢平台应用版本
	 */
	@Override
	public SpapDimAppVersion findAppVersionByName(int appPlatId, int appId, String appVersionName) {
		return this.dimAppVersionDao.findAppVersionByName(dimDataSource, appPlatId, appId, appVersionName);
	}

	/**
	 * 保存平台应用版本
	 */
	@Override
	public void saveDimAppVersion(SpapDimAppVersion newAppVersion) {
		this.dimAppVersionDao.saveDimAppVersion(dimDataSource, newAppVersion);
	}

	
	/**
	 * Guave平台应用版本维表Key
	 *
	 */
	private static class DimAppVersionKey {
		private int appPlatId;
		private int appId;
		private String appVersionName;
		private int hash;
		
		DimAppVersionKey(int appPlatId, int appId, String appVersionName) {
			this.appPlatId =appPlatId;
			this.appId = appId;
			this.appVersionName = appVersionName;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if(hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + appPlatId;
				result = prime * result + appId;
				result = prime * result + ((appVersionName == null) ? 0 : appVersionName.hashCode());
				hash = result;
			}
			
			return hash;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DimAppVersionKey other = (DimAppVersionKey) obj;
			if (appPlatId != other.appPlatId)
				return false;
			if (appId != other.appId)
				return false;
			if (appVersionName == null) {
				if (other.appVersionName != null)
					return false;
			} else if (!appVersionName.equals(other.appVersionName))
				return false;
			return true;
		}
	}
	
	/**
	 * 平台应用版本Guave
	 *
	 */
	private static class DimAppVersionKeyCacheLoader extends CacheLoader<DimAppVersionKey, SpapDimAppVersion> {
		private SpapOdsDimAppVersionService odsDimAppVersionService;
		private Lock lock;

		public DimAppVersionKeyCacheLoader(SpapOdsDimAppVersionService odsDimAppVersionService) {
			this.odsDimAppVersionService = odsDimAppVersionService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimAppVersion load(DimAppVersionKey dimAppVersionKey) throws Exception {
			SpapDimAppVersion dimAppVersion = odsDimAppVersionService.findAppVersionByName(dimAppVersionKey.appPlatId, dimAppVersionKey.appId, dimAppVersionKey.appVersionName);
			if (dimAppVersion == null) {
				this.lock.lock();
				try {
					dimAppVersion = this.odsDimAppVersionService.findAppVersionByName(dimAppVersionKey.appPlatId, dimAppVersionKey.appId, dimAppVersionKey.appVersionName);
					if (dimAppVersion == null) {
						this.odsDimAppVersionService.saveDimAppVersion(new SpapDimAppVersion((short) 0, dimAppVersionKey.appPlatId, dimAppVersionKey.appId, dimAppVersionKey.appVersionName));
						dimAppVersion = this.odsDimAppVersionService.findAppVersionByName(dimAppVersionKey.appPlatId, dimAppVersionKey.appId, dimAppVersionKey.appVersionName);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimAppVersion == null ? APPVERSION_NOT_PRESENT : (SpapDimAppVersion) dimAppVersion.clone();
		}
	}

}
