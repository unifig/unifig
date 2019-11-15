package etl.dispatch.java.spap.ods.service.impl;

import java.util.List;
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
import etl.dispatch.java.spap.ods.dao.SpapDimCityDao;
import etl.dispatch.java.spap.ods.domain.SpapDimCity;
import etl.dispatch.java.spap.ods.service.SpapOdsDimCityService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class SpapOdsDimCityServiceImpl implements SpapOdsDimCityService {
	private final static Logger logger = LoggerFactory.getLogger(SpapOdsDimCityServiceImpl.class);
	// 设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static SpapDimCity NULL_NOT_PRESENT = new SpapDimCity(); 
	
	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimCityDao dimCityDao;
	private LoadingCache<DimCityKey, SpapDimCity> dimCityKeyCache;
	private LoadingCache<DimCityIdKey, SpapDimCity> dimCityIdKeyCache;

	public SpapOdsDimCityServiceImpl() {
		this.dimCityKeyCache = CacheBuilder.newBuilder()
				                .concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                .build(new DimCityKeyCacheLoader(this));
		this.dimCityIdKeyCache = CacheBuilder.newBuilder()
				                .concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                .build(new DimCityIdKeyCacheLoader(this));
	}

	@Override
	public SpapDimCity getCityByName(int countryId, int regionId, String cityName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(cityName)) {
			return null;
		}
		SpapDimCity dimCity = null;
		try {
			DimCityKey dimCityKey = new DimCityKey(countryId, regionId, cityName);
			if (createIfNotExist) {
				dimCity = this.dimCityKeyCache.get(dimCityKey);
			} else {
				dimCity = this.dimCityKeyCache.getIfPresent(dimCityKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimCity by DimCityKey from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCity by DimCityKey from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCity by DimCityKey from cache: " + e.getMessage(), e);
		}
		return dimCity == null || StringUtil.isNullOrEmpty(dimCity.getName()) ? null : dimCity;
	}

	@Override
	public SpapDimCity findCityByName(int countryId, int regionId, String cityName) {
		if (cityName == null || cityName.length() == 0)
			return null;
		List<SpapDimCity> rs = this.dimCityDao.findCityByName(dimDataSource, new NewMapUtil("countryId", countryId).set("regionId", regionId).set("cityName", cityName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public SpapDimCity getCityByCityId(int id, int countryId, int regionId) {
		SpapDimCity dimCity = null;
		try {
			dimCity = dimCityIdKeyCache.get(new DimCityIdKey(id, countryId, regionId));
		} catch (ExecutionException e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimCity by DimCityIdKey from cache:  " + t.getMessage(), t);
		}
		return dimCity == null ? null : dimCity;
	}

	@Override
	public SpapDimCity findCityByCityId(int id, int countryId, int regionId) {
		if (id == 0 && countryId == 0 && regionId == 0)
			return null;
		List<SpapDimCity> rs = this.dimCityDao.findCityByCityId(dimDataSource, new NewMapUtil("cityId", id).set("countryId", countryId).set("regionId", regionId).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimCity(SpapDimCity dimCity) {
		this.dimCityDao.saveDimCity(dimDataSource, dimCity);
	}

	private static class DimCityKey {
		private int countryId;
		private int regionId;
		private String name;
		private int hash;

		public DimCityKey(int countryId, int regionId, String name) {
			this.countryId = countryId;
			this.regionId = regionId;
			this.name = name;
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + countryId;
				result = prime * result + regionId;
				result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			DimCityKey other = (DimCityKey) obj;
			if (countryId != other.countryId)
				return false;
			if (regionId != other.regionId)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}

	private static class DimCityKeyCacheLoader extends CacheLoader<DimCityKey, SpapDimCity> {
		private SpapOdsDimCityService odsDimCityService;
		private Lock lock;

		DimCityKeyCacheLoader(SpapOdsDimCityService odsDimCityService) {
			this.odsDimCityService = odsDimCityService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimCity load(DimCityKey dimCityKey) throws Exception {
			SpapDimCity dimCity = odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
			if (dimCity == null) {
				this.lock.lock();
				try {
					dimCity = this.odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
					if (dimCity == null) {
						SpapDimCity newDimCity = new SpapDimCity(0, dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
						this.odsDimCityService.saveDimCity(newDimCity);
						dimCity = this.odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimCity == null ? NULL_NOT_PRESENT : (SpapDimCity) dimCity.clone();
		}
	}

	private static class DimCityIdKey {
		private int cityId;
		private int countryId;
		private int regionId;
		private int hash;

		public DimCityIdKey(int cityId, int countryId, int regionId) {
			this.cityId = cityId;
			this.countryId = countryId;
			this.regionId = regionId;
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + cityId;
				result = prime * result + countryId;
				result = prime * result + regionId;
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
			DimCityIdKey other = (DimCityIdKey) obj;
			if (cityId != other.cityId)
				return false;
			if (countryId != other.countryId)
				return false;
			if (regionId != other.regionId)
				return false;
			return true;
		}
	}

	private static class DimCityIdKeyCacheLoader extends CacheLoader<DimCityIdKey, SpapDimCity> {
		private SpapOdsDimCityService odsDimCityService;

		DimCityIdKeyCacheLoader(SpapOdsDimCityService odsDimCityService) {
			this.odsDimCityService = odsDimCityService;
		}

		@Override
		public SpapDimCity load(DimCityIdKey dimCityIdKey) throws Exception {
			SpapDimCity dimCity = odsDimCityService.findCityByCityId(dimCityIdKey.cityId, dimCityIdKey.countryId, dimCityIdKey.regionId);
			return dimCity == null ? NULL_NOT_PRESENT : (SpapDimCity) dimCity.clone();
		}
	}
}


