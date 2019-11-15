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
import etl.dispatch.java.spap.ods.dao.SpapDimIspDao;
import etl.dispatch.java.spap.ods.domain.SpapDimIsp;
import etl.dispatch.java.spap.ods.service.SpapOdsDimIspService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;
@Service
public class SpapOdsDimIspServiceImpl implements SpapOdsDimIspService {
	private final static Logger logger = LoggerFactory.getLogger(SpapOdsDimIspServiceImpl.class);
	//设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作  
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值  
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static SpapDimIsp NULL_NOT_PRESENT = new SpapDimIsp(); 
	
	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimIspDao dimIspDao;
	private LoadingCache<DimIspKey, SpapDimIsp> dimIspKeyCache;
	private LoadingCache<Integer, SpapDimIsp>   dimIspIdKeyCache;
	public SpapOdsDimIspServiceImpl() {
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
	public SpapDimIsp getIspByName(String ispName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(ispName)) {
			return null;
		}
		SpapDimIsp dimIsp = null;
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
	public SpapDimIsp findIspByName(String ispName) {
		if (ispName == null || ispName.length() == 0)
			return null;
		List<SpapDimIsp> rs = this.dimIspDao.findIspByName(dimDataSource, new NewMapUtil("ispName", ispName).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}

	@Override
	public SpapDimIsp getIspByIspId(int id) {
		SpapDimIsp dimIspInfo = null;
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
	public SpapDimIsp findIspByIspId(int id) {
		List<SpapDimIsp> rs = this.dimIspDao.findIspByIspId(dimDataSource, id);
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimIsp(SpapDimIsp dimIsp) {
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
	
	private static class DimIspKeyCacheLoader extends CacheLoader<DimIspKey, SpapDimIsp> {
		private SpapOdsDimIspService odsDimIspService;
		private Lock lock;

		DimIspKeyCacheLoader(SpapOdsDimIspService odsDimIspService) {
			this.odsDimIspService = odsDimIspService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimIsp load(DimIspKey dimIspKey) throws Exception {
			SpapDimIsp dimIsp = odsDimIspService.findIspByName(dimIspKey.name);
			if (dimIsp == null) {
				this.lock.lock();
				try {
					dimIsp = this.odsDimIspService.findIspByName(dimIspKey.name);
					if (dimIsp == null) {
						SpapDimIsp newDimIsp = new SpapDimIsp(0, dimIspKey.name);
						this.odsDimIspService.saveDimIsp(newDimIsp);
						dimIsp = this.odsDimIspService.findIspByName(dimIspKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimIsp == null ? NULL_NOT_PRESENT : (SpapDimIsp) dimIsp.clone();
		}
	}
	
	private static class DimIspIdKeyCacheLoader extends CacheLoader<Integer, SpapDimIsp> {
		private SpapOdsDimIspService odsDimIspService;

		DimIspIdKeyCacheLoader(SpapOdsDimIspService odsDimIspService) {
			this.odsDimIspService = odsDimIspService;
		}

		@Override
		public SpapDimIsp load(Integer ispId) throws Exception {
			SpapDimIsp dimIsp = odsDimIspService.findIspByIspId(ispId);
			return dimIsp == null ? NULL_NOT_PRESENT : (SpapDimIsp) dimIsp.clone();
		}
	}
	
}
