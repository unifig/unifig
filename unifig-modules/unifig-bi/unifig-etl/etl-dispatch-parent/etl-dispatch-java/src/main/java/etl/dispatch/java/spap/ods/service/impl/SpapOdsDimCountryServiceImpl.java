package etl.dispatch.java.spap.ods.service.impl;

import java.util.HashMap;
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
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.java.spap.ods.dao.SpapDimCountryDao;
import etl.dispatch.java.spap.ods.domain.SpapDimCountry;
import etl.dispatch.java.spap.ods.domain.SpapDimCountryCode;
import etl.dispatch.java.spap.ods.service.SpapOdsDimCountryService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class SpapOdsDimCountryServiceImpl implements SpapOdsDimCountryService , ScheduledService {
	private final static Logger logger = LoggerFactory.getLogger(SpapOdsDimCountryServiceImpl.class);
	//设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作  
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值  
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static SpapDimCountry NULL_NOT_PRESENT = new SpapDimCountry(); 
	
	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimCountryDao dimCountryDao;
	private Map<String, SpapDimCountryCode> countryCodes;
	private LoadingCache<DimCountryKey, SpapDimCountry> dimCountryKeyCache;
	
	private LoadingCache<Integer, SpapDimCountry> dimCountryIdKeyCache;
	public SpapOdsDimCountryServiceImpl() {
		this.dimCountryKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimCountryKeyCacheLoader(this));
		this.dimCountryIdKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimCountryIdKeyCacheLoader(this));
		this.countryCodes = new HashMap<String, SpapDimCountryCode>();
	}
	
	
	@Override
	public SpapDimCountry getCountryByName(String countryCode, String countryName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(countryName)) {
			return null;
		}
		SpapDimCountry dimCountry = null;
		try {
			DimCountryKey dimCountryKey = new DimCountryKey(countryCode, countryName);
			if (createIfNotExist) {
				dimCountry = dimCountryKeyCache.get(dimCountryKey);
			} else {
				dimCountry = dimCountryKeyCache.getIfPresent(dimCountryKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimCountry by dimCountryKeyCache from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCountry by dimCountryKeyCache from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCountry by dimCountryKeyCache from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimCountry by dimCountryKeyCache from cache:  " + t.getMessage(), t);
		}
		return dimCountry == null ? null : dimCountry;
	}

	@Override
	public SpapDimCountry findCountryByName(String countryCode, String countryName) {
		if (countryCode == null || countryCode.length() == 0)
			return null;
		if (countryName == null || countryName.length() == 0)
			return null;
		List<SpapDimCountry> rs = this.dimCountryDao.findCountryByName(dimDataSource, new NewMapUtil("countryCode", countryCode).set("countryName", countryName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public SpapDimCountry getCountryByCountryId(int id) {
		SpapDimCountry dimCountry = null;
		try {
			dimCountry = dimCountryIdKeyCache.get(id);
		} catch (ExecutionException e) {
			logger.error("error to load dimCountry by Integer countryId from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimCountry by Integer countryId from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimCountry by Integer countryId from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimCountry by Integer countryId from cache:  " + t.getMessage(), t);
		}
		return dimCountry == null ? null : dimCountry;
	}

	@Override
	public SpapDimCountry findCountryByCountryId(int id) {
		List<SpapDimCountry> rs = this.dimCountryDao.findCountryByCountryId(dimDataSource, id);
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimCountry(SpapDimCountry dimCountry) {
		this.dimCountryDao.saveDimCountry(dimDataSource, dimCountry);
	}
	
	@Override
	public SpapDimCountryCode getCountryCodeByName(String countryName) {
		return this.countryCodes.get(countryName);
	}
	
	private static class DimCountryKey {
		private String code;
		private String name;
		private int hash;

		public DimCountryKey(String code, String name) {
			this.code =code;
			this.name = name;
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((code == null) ? 0 : code.hashCode());
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
			DimCountryKey other = (DimCountryKey) obj;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	private static class DimCountryKeyCacheLoader extends CacheLoader<DimCountryKey, SpapDimCountry> {
		private SpapOdsDimCountryService odsDimCountryService;
		private Lock lock;

		DimCountryKeyCacheLoader(SpapOdsDimCountryService odsDimCountryService) {
			this.odsDimCountryService = odsDimCountryService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimCountry load(DimCountryKey dimCountryKey) throws Exception {
			SpapDimCountry dimCountry = odsDimCountryService.findCountryByName(dimCountryKey.code, dimCountryKey.name);
			if (dimCountry == null) {
				this.lock.lock();
				try {
					dimCountry = this.odsDimCountryService.findCountryByName(dimCountryKey.code, dimCountryKey.name);
					if (dimCountry == null) {
						SpapDimCountry newDimCountry = new SpapDimCountry(0, dimCountryKey.code, dimCountryKey.name);
						this.odsDimCountryService.saveDimCountry(newDimCountry);
						dimCountry = this.odsDimCountryService.findCountryByName(dimCountryKey.code, dimCountryKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimCountry == null ? NULL_NOT_PRESENT : (SpapDimCountry) dimCountry.clone();
		}
	}
	
	private static class DimCountryIdKeyCacheLoader extends CacheLoader<Integer, SpapDimCountry> {
		private SpapOdsDimCountryService odsDimCountryService;

		DimCountryIdKeyCacheLoader(SpapOdsDimCountryService odsDimCountryService) {
			this.odsDimCountryService = odsDimCountryService;
		}

		@Override
		public SpapDimCountry load(Integer countryId) throws Exception {
			SpapDimCountry dimCountry = odsDimCountryService.findCountryByCountryId(countryId);
			return dimCountry == null ? NULL_NOT_PRESENT : (SpapDimCountry) dimCountry.clone();
		}
	}

	@Override
	public String getName() {
		return "odsDimCountryServiceImpl";
	}

	@Override
	public void schedule() {
		List<SpapDimCountryCode> countryCodeList = this.dimCountryDao.findCountryCode(dimDataSource);
		if (null != countryCodeList && !countryCodeList.isEmpty()) {
			Map<String, SpapDimCountryCode> countryCodes = new HashMap<String, SpapDimCountryCode>();
			for (SpapDimCountryCode countryCode : countryCodeList) {
				countryCodes.put(countryCode.getCnName(), countryCode);
			}
			this.countryCodes = countryCodes;
		}
	}
	
}
