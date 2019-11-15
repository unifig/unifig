package etl.dispatch.java.ods.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.GuavaCacheScript;
import etl.dispatch.java.ods.dao.DimAppPlatDao;
import etl.dispatch.java.ods.domain.DimAppPlat;
import etl.dispatch.java.ods.enums.DimAppPlatEnum;
import etl.dispatch.java.ods.service.OdsDimAppPlatService;
import etl.dispatch.util.StringUtil;

@Service
public class OdsDimAppPlatServiceImpl implements OdsDimAppPlatService, GuavaCacheScript {
	private static Logger logger = LoggerFactory.getLogger(OdsDimAppPlatServiceImpl.class);
	// 平台应用类型维表字典转换 ; dim_app_plat
	private LoadingCache<DimAppPlatKey, DimAppPlat> dimAppPlatKeyCache;
	private final static DimAppPlat APPPLAT_NOT_PRESENT = new DimAppPlat();

	@Autowired
	private DimDataSource dimDataSource;

	@Autowired
	private DimAppPlatDao dimAppPlatDao;

	public OdsDimAppPlatServiceImpl() {
		// 平台应用类型Guave緩存
		this.dimAppPlatKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				.maximumSize(DEFAULT_CACHE_MAX_SIZE)
				.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				.build(new DimAppPlatKeyCacheLoader(this));
	}
	
	@Override
	public List<DimAppPlat> getAllAppPlat() {
		List<DimAppPlat> appPlats = new ArrayList<DimAppPlat>();
		this.dimAppPlatKeyCache.getAllPresent(appPlats);
		return appPlats == null ? new ArrayList<DimAppPlat>() : appPlats;
	}

	/**
	 * Guave查詢平台应用类型
	 */
	@Override
	public DimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(appPlatName)) {
			return null;
		}
		// 平台应用Name过滤
		String toLowerCase = appPlatName.toLowerCase();
		Map<String, DimAppPlatEnum> dimAppPlatMap = DimAppPlatEnum.lookup;
		if (null != dimAppPlatMap.get(toLowerCase)) {
			appPlatName = dimAppPlatMap.get(toLowerCase).getDesc();
		}else{
            appPlatName = DimAppPlatEnum.Unknown.getDesc();
		}
		if (appPlatName.length() > DEFAULT_NAME_MAX_LENGTH) {
			appPlatName = appPlatName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		DimAppPlat dimAppPlat = null;
		try {
			DimAppPlatKey dimAppPlatKey = new DimAppPlatKey(appPlatName);
			if (createIfNotExist) {
				dimAppPlat = this.dimAppPlatKeyCache.get(dimAppPlatKey);
			} else {
				dimAppPlat = this.dimAppPlatKeyCache.getIfPresent(dimAppPlatKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimAppPlat from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimAppPlat from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimAppPlat from cache: " + e.getMessage(), e);
		}
		return dimAppPlat == null ? APPPLAT_NOT_PRESENT : dimAppPlat;
	}

	/**
	 * 數據庫查詢平台应用类型
	 */
	@Override
	public DimAppPlat findAppPlatByName(String appPlatName) {
		return this.dimAppPlatDao.findAppPlatByName(dimDataSource, appPlatName);
	}

	/**
	 * 保存平台应用类型
	 */
	@Override
	public void saveDimAppPlat(DimAppPlat newAppPlat) {
		this.dimAppPlatDao.saveDimAppPlat(dimDataSource, newAppPlat);
	}

	/**
	 * Guave平台应用类型维表Key
	 * 
	 *
	 */
	private static class DimAppPlatKey {
		private String name;
		private String nameLowercase;

		public DimAppPlatKey(String name) {
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
			DimAppPlatKey other = (DimAppPlatKey) obj;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}

	/**
	 * 平台应用类型Guave
	 * 
	 *
	 */
	private static class DimAppPlatKeyCacheLoader extends CacheLoader<DimAppPlatKey, DimAppPlat> {
		private OdsDimAppPlatService odsDimAppPlatService;
		private Lock lock;

		public DimAppPlatKeyCacheLoader(OdsDimAppPlatService odsDimAppPlatService) {
			this.odsDimAppPlatService = odsDimAppPlatService;
			this.lock = new ReentrantLock();
		}

		@Override
		public DimAppPlat load(DimAppPlatKey dimAppPlatKey) throws Exception {
			DimAppPlat dimAppPlat = odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
			if (dimAppPlat == null) {
				this.lock.lock();
				try {
					dimAppPlat = this.odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
					if (dimAppPlat == null) {
						this.odsDimAppPlatService.saveDimAppPlat(new DimAppPlat((short) 0, dimAppPlatKey.name));
						dimAppPlat = this.odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimAppPlat == null ? APPPLAT_NOT_PRESENT : (DimAppPlat) dimAppPlat.clone();
		}
	}

}
