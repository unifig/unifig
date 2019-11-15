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
import etl.dispatch.java.ods.dao.DimIspDao;
import etl.dispatch.java.ods.domain.DimIsp;
import etl.dispatch.java.ods.service.OdsDimIspService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class OdsDimIspServiceImpl implements OdsDimIspService {
	private final static Logger logger = LoggerFactory.getLogger(OdsDimIspServiceImpl.class);
	//设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作  
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值  
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static DimIsp NULL_NOT_PRESENT = new DimIsp(); 
	
	@Autowired
	private DimDataSource dimDataSource;
	
	@Autowired
	private DimIspDao dimIspDao;
	private LoadingCache<DimIspKey, DimIsp> dimIspKeyCache;
	private LoadingCache<Integer, DimIsp>   dimIspIdKeyCache;
	public OdsDimIspServiceImpl() {
		this.dimIspKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimIspKeyCacheLoader(this));
		this.dimIspIdKeyCache = CacheBuilder.newBuilder()
										.concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
										.initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
										.maximumSize(DEFAULT_CACHE_MAX_SIZE)
										.expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
										.build(new DimIspIdKeyCacheLoader(this));
	}
	
	
	@Override
	public DimIsp getIspByName(String ispName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(ispName)) {
			return null;
		}
		DimIsp dimIsp = null;
		try {
			DimIspKey dimIspKey = new DimIspKey(ispName);
			if (createIfNotExist) {
				dimIsp = dimIspKeyCache.get(dimIspKey);
			} else {
				dimIsp = dimIspKeyCache.getIfPresent(dimIspKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimIspInfo by dimIspKeyCache from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIspInfo by dimIspKeyCache from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIspInfo by dimIspKeyCache from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimIspInfo by dimIspKeyCache from cache:  " + t.getMessage(), t);
		}
		return dimIsp == null ? null : dimIsp;
	}

	@Override
	public DimIsp findIspByName(String ispName) {
		if (ispName == null || ispName.length() == 0)
			return null;
		List<DimIsp> rs = this.dimIspDao.findIspByName(dimDataSource, new NewMapUtil("ispName", ispName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public DimIsp getIspByIspId(int id) {
		DimIsp dimIspInfo = null;
		try {
			dimIspInfo = dimIspIdKeyCache.get(id);
		} catch (ExecutionException e) {
			logger.error("error to load dimIspInfo by dimIspIdKeyCache from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIspInfo by dimIspIdKeyCache from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIspInfo by dimIspIdKeyCache from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimIspInfo by dimIspIdKeyCache from cache:  " + t.getMessage(), t);
		}
		return dimIspInfo == null ? null : dimIspInfo;
	}


	@Override
	public DimIsp findIspByIspId(int id) {
		List<DimIsp> rs = this.dimIspDao.findIspByIspId(dimDataSource, id);
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimIsp(DimIsp dimIsp) {
		this.dimIspDao.saveDimIsp(dimDataSource,dimIsp);
	}
	
	private static class DimIspKey {
		private String name;
		private int hash;

		public DimIspKey(String name) {
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
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
			DimIspKey other = (DimIspKey) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	private static class DimIspKeyCacheLoader extends CacheLoader<DimIspKey, DimIsp> {
		private OdsDimIspService odsDimIspService;
		private Lock lock;

		DimIspKeyCacheLoader(OdsDimIspService odsDimIspService) {
			this.odsDimIspService = odsDimIspService;
			this.lock = new ReentrantLock();
		}

		@Override
		public DimIsp load(DimIspKey dimIspKey) throws Exception {
			DimIsp dimIsp = odsDimIspService.findIspByName(dimIspKey.name);
			if (dimIsp == null) {
				this.lock.lock();
				try {
					dimIsp = this.odsDimIspService.findIspByName(dimIspKey.name);
					if (dimIsp == null) {
						DimIsp newDimIsp = new DimIsp(0, dimIspKey.name);
						this.odsDimIspService.saveDimIsp(newDimIsp);
						dimIsp = this.odsDimIspService.findIspByName(dimIspKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimIsp == null ? NULL_NOT_PRESENT : (DimIsp) dimIsp.clone();
		}
	}
	
	private static class DimIspIdKeyCacheLoader extends CacheLoader<Integer, DimIsp> {
		private OdsDimIspService odsDimIspService;

		DimIspIdKeyCacheLoader(OdsDimIspService odsDimIspService) {
			this.odsDimIspService = odsDimIspService;
		}

		@Override
		public DimIsp load(Integer ispId) throws Exception {
			DimIsp dimIsp = odsDimIspService.findIspByIspId(ispId);
			return dimIsp == null ? NULL_NOT_PRESENT : (DimIsp) dimIsp.clone();
		}
	}
	
}
