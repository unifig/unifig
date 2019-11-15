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
import etl.dispatch.java.spap.ods.dao.SpapDimRegionDao;
import etl.dispatch.java.spap.ods.domain.SpapDimRegion;
import etl.dispatch.java.spap.ods.service.SpapOdsDimRegionService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class SpapOdsDimRegionServiceImpl implements SpapOdsDimRegionService {
	private final static Logger logger = LoggerFactory.getLogger(SpapOdsDimRegionServiceImpl.class);
	//设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作  
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值  
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static SpapDimRegion NULL_NOT_PRESENT = new SpapDimRegion();
	
	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimRegionDao dimRegionDao;
	private LoadingCache<DimRegionKey, SpapDimRegion> dimRegionKeyCache;
	private LoadingCache<DimRegionIdKey, SpapDimRegion> dimRegionIdKeyCache;
	
	public SpapOdsDimRegionServiceImpl() {
		this.dimRegionKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimRegionKeyCacheLoader(this));
		this.dimRegionIdKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimRegionIdKeyCacheLoader(this));
	}
	
	@Override
	public SpapDimRegion getDimRegionByName(int countryId, String regionName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(regionName)) {
			return null;
		}
		SpapDimRegion dimRegion = null;
		try {
			DimRegionKey dimRegionKey = new DimRegionKey(countryId, regionName);
			if (createIfNotExist) {
				dimRegion = this.dimRegionKeyCache.get(dimRegionKey);
			} else {
				dimRegion = this.dimRegionKeyCache.getIfPresent(dimRegionKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimCountry model from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCountry model from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCountry model from cache: " + e.getMessage(), e);
		}
		return dimRegion == null ? null : dimRegion;
	}
	
	@Override
	public SpapDimRegion findDimRegionByName(int countryId, String regionName) {
		if (countryId == 0)
			return null;

		if (regionName == null || regionName.length() == 0)
			return null;

		List<SpapDimRegion> rs = this.dimRegionDao.findDimRegionByName(dimDataSource, new NewMapUtil("countryId", countryId).set("regionName", regionName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public SpapDimRegion getDimRegionByRegionId(int id, int countryId) {
		SpapDimRegion dimRegion = null;
		try {
			dimRegion = dimRegionIdKeyCache.get(new DimRegionIdKey(id, countryId));
		} catch (ExecutionException e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCity by DimCityIdKey from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimCity by DimCityIdKey from cache:  " + t.getMessage(), t);
		}
		return dimRegion == null ? null : dimRegion;
	}

	@Override
	public SpapDimRegion findDimRegionByRegionId(int id, int countryId) {
		List<SpapDimRegion> rs = this.dimRegionDao.findDimRegionByRegionId(dimDataSource, new NewMapUtil("regionId", id).set("countryId", countryId).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public void saveDimRegion(SpapDimRegion dimRegion) {
		this.dimRegionDao.saveDimRegion(dimDataSource,dimRegion);
	}
	
	private static class DimRegionKey {
		private int countryId;
		private String name;
		private int hash;
		
		DimRegionKey(int countryId, String name) {
			this.countryId = countryId;
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			if(hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + countryId;
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
			DimRegionKey other = (DimRegionKey) obj;
			if (countryId != other.countryId)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	private static class DimRegionKeyCacheLoader extends CacheLoader<DimRegionKey, SpapDimRegion> {
		private SpapOdsDimRegionService odsDimRegionService;
		private Lock lock;

		DimRegionKeyCacheLoader(SpapOdsDimRegionService odsDimRegionService) {
			this.odsDimRegionService = odsDimRegionService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimRegion load(DimRegionKey dimRegionKey) throws Exception {
			SpapDimRegion dimRegion = odsDimRegionService.findDimRegionByName(dimRegionKey.countryId, dimRegionKey.name);
			if (dimRegion == null) {
				this.lock.lock();
				try {
					dimRegion = this.odsDimRegionService.findDimRegionByName(dimRegionKey.countryId, dimRegionKey.name);
					if (dimRegion == null) {
						SpapDimRegion newDimRegion = new SpapDimRegion(0, dimRegionKey.countryId, dimRegionKey.name);
						this.odsDimRegionService.saveDimRegion(newDimRegion);
						dimRegion = this.odsDimRegionService.findDimRegionByName(dimRegionKey.countryId, dimRegionKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimRegion == null ? NULL_NOT_PRESENT : (SpapDimRegion) dimRegion.clone();
		}
	}
	
	private static class DimRegionIdKey {
		private int regionId;
		private int countryId;
		private int hash;
		
		DimRegionIdKey(int regionId, int countryId) {
			this.regionId =regionId;
			this.countryId = countryId;
		}

		@Override
		public int hashCode() {
			if(hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + regionId;
				result = prime * result + countryId;
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
			DimRegionIdKey other = (DimRegionIdKey) obj;
			if (regionId != other.regionId)
				return false;
			if (countryId != other.countryId)
				return false;
			return true;
		}
	}
	
	private static class DimRegionIdKeyCacheLoader extends CacheLoader<DimRegionIdKey, SpapDimRegion> {
		private SpapOdsDimRegionService odsDimRegionService;

		DimRegionIdKeyCacheLoader(SpapOdsDimRegionService odsDimRegionService) {
			this.odsDimRegionService = odsDimRegionService;
		}

		@Override
		public SpapDimRegion load(DimRegionIdKey dimRegionIdKey) throws Exception {
			SpapDimRegion dimRegion = odsDimRegionService.findDimRegionByRegionId(dimRegionIdKey.regionId, dimRegionIdKey.countryId);
			return dimRegion == null ? NULL_NOT_PRESENT : (SpapDimRegion) dimRegion.clone();
		}
	}

}
