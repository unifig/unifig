package etl.dispatch.java.ods.service.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

import etl.dispatch.base.datasource.DimDataSource;
import etl.dispatch.java.ods.dao.DimCityDao;
import etl.dispatch.java.ods.domain.DimCity;
import etl.dispatch.java.ods.service.OdsDimCityService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class OdsDimCityServiceImpl implements OdsDimCityService {
	private final static Logger logger = LoggerFactory.getLogger(OdsDimCityServiceImpl.class);
	// 设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static DimCity NULL_NOT_PRESENT = new DimCity(); 
	
	@Autowired
	private DimDataSource dimDataSource;
	
	@Autowired
	private DimCityDao dimCityDao;
	private LoadingCache<DimCityKey, DimCity> dimCityKeyCache;
	private LoadingCache<DimCityIdKey, DimCity> dimCityIdKeyCache;

	public OdsDimCityServiceImpl() {
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
	public DimCity getCityByName(int countryId, int regionId, String cityName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(cityName)) {
			return null;
		}
		DimCity dimCity = null;
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
	public DimCity findCityByName(int countryId, int regionId, String cityName) {
		if (cityName == null || cityName.length() == 0)
			return null;
		List<DimCity> rs = this.dimCityDao.findCityByName(dimDataSource, new NewMapUtil("countryId", countryId).set("regionId", regionId).set("cityName", cityName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public DimCity getCityByCityId(int id, int countryId, int regionId) {
		DimCity dimCity = null;
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
	public DimCity findCityByCityId(int id, int countryId, int regionId) {
		if (id == 0 && countryId == 0 && regionId == 0)
			return null;
		List<DimCity> rs = this.dimCityDao.findCityByCityId(dimDataSource, new NewMapUtil("cityId", id).set("countryId", countryId).set("regionId", regionId).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimCity(DimCity dimCity) {
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

	private static class DimCityKeyCacheLoader extends CacheLoader<DimCityKey, DimCity> {
		private OdsDimCityService odsDimCityService;
		private Lock lock;

		DimCityKeyCacheLoader(OdsDimCityService odsDimCityService) {
			this.odsDimCityService = odsDimCityService;
			this.lock = new ReentrantLock();
		}

		@Override
		public DimCity load(DimCityKey dimCityKey) throws Exception {
			DimCity dimCity = odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
			if (dimCity == null) {
				this.lock.lock();
				try {
					dimCity = this.odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
					if (dimCity == null) {
						DimCity newDimCity = new DimCity(0, dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
						this.odsDimCityService.saveDimCity(newDimCity);
						dimCity = this.odsDimCityService.findCityByName(dimCityKey.countryId, dimCityKey.regionId, dimCityKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimCity == null ? NULL_NOT_PRESENT : (DimCity) dimCity.clone();
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

	private static class DimCityIdKeyCacheLoader extends CacheLoader<DimCityIdKey, DimCity> {
		private OdsDimCityService odsDimCityService;

		DimCityIdKeyCacheLoader(OdsDimCityService odsDimCityService) {
			this.odsDimCityService = odsDimCityService;
		}

		@Override
		public DimCity load(DimCityIdKey dimCityIdKey) throws Exception {
			DimCity dimCity = odsDimCityService.findCityByCityId(dimCityIdKey.cityId, dimCityIdKey.countryId, dimCityIdKey.regionId);
			return dimCity == null ? NULL_NOT_PRESENT : (DimCity) dimCity.clone();
		}
	}
}


