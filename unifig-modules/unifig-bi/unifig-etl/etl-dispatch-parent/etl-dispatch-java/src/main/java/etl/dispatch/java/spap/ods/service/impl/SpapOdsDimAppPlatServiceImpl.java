package etl.dispatch.java.spap.ods.service.impl;

import java.util.ArrayList;
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
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.base.datasource.SpapDimDataSource;
import etl.dispatch.java.spap.ods.GuavaCacheSpap;
import etl.dispatch.java.spap.ods.dao.SpapDimAppPlatDao;
import etl.dispatch.java.spap.ods.domain.SpapDimAppPlat;
import etl.dispatch.java.spap.ods.enums.SpapDimAppPlatEnum;
import etl.dispatch.java.spap.ods.service.SpapOdsDimAppPlatService;
import etl.dispatch.util.StringUtil;

@Service
public class SpapOdsDimAppPlatServiceImpl implements SpapOdsDimAppPlatService, GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimAppPlatServiceImpl.class);
	// 平台应用类型维表字典转换 ; dim_app_plat
	private LoadingCache<DimAppPlatKey, SpapDimAppPlat> dimAppPlatKeyCache;
	private final static SpapDimAppPlat APPPLAT_NOT_PRESENT = new SpapDimAppPlat();

	@Autowired
	private SpapDimDataSource dimDataSource;

	@Autowired
	private SpapDimAppPlatDao dimAppPlatDao;

	public SpapOdsDimAppPlatServiceImpl() {
		// 平台应用类型Guave緩存
		this.dimAppPlatKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				.maximumSize(DEFAULT_CACHE_MAX_SIZE)
				.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				.build(new DimAppPlatKeyCacheLoader(this));
	}
	
	@Override
	public List<SpapDimAppPlat> getAllAppPlat() {
		List<SpapDimAppPlat> appPlats = new ArrayList<SpapDimAppPlat>();
		this.dimAppPlatKeyCache.getAllPresent(appPlats);
		return appPlats == null ? new ArrayList<SpapDimAppPlat>() : appPlats;
	}

	/**
	 * Guave查詢平台应用类型
	 */
	@Override
	public SpapDimAppPlat getAppPlatByName(String appPlatName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(appPlatName)) {
			return null;
		}
		// 平台应用Name过滤
		String toLowerCase = appPlatName.toLowerCase();
		Map<String, SpapDimAppPlatEnum> dimAppPlatMap = SpapDimAppPlatEnum.lookup;
		if (null != dimAppPlatMap.get(toLowerCase)) {
			appPlatName = dimAppPlatMap.get(toLowerCase).getDesc();
		}else{
            appPlatName = SpapDimAppPlatEnum.Unknown.getDesc();
		}
		if (appPlatName.length() > DEFAULT_NAME_MAX_LENGTH) {
			appPlatName = appPlatName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimAppPlat dimAppPlat = null;
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
	public SpapDimAppPlat findAppPlatByName(String appPlatName) {
		return this.dimAppPlatDao.findAppPlatByName(dimDataSource, appPlatName);
	}

	/**
	 * 保存平台应用类型
	 */
	@Override
	public void saveDimAppPlat(SpapDimAppPlat newAppPlat) {
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
	private static class DimAppPlatKeyCacheLoader extends CacheLoader<DimAppPlatKey, SpapDimAppPlat> {
		private SpapOdsDimAppPlatService odsDimAppPlatService;
		private Lock lock;

		public DimAppPlatKeyCacheLoader(SpapOdsDimAppPlatService odsDimAppPlatService) {
			this.odsDimAppPlatService = odsDimAppPlatService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimAppPlat load(DimAppPlatKey dimAppPlatKey) throws Exception {
			SpapDimAppPlat dimAppPlat = odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
			if (dimAppPlat == null) {
				this.lock.lock();
				try {
					dimAppPlat = this.odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
					if (dimAppPlat == null) {
						this.odsDimAppPlatService.saveDimAppPlat(new SpapDimAppPlat((short) 0, dimAppPlatKey.name));
						dimAppPlat = this.odsDimAppPlatService.findAppPlatByName(dimAppPlatKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimAppPlat == null ? APPPLAT_NOT_PRESENT : (SpapDimAppPlat) dimAppPlat.clone();
		}
	}

}
