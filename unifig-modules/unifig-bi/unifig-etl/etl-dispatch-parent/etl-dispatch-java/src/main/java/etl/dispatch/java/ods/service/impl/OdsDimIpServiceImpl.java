package etl.dispatch.java.ods.service.impl;

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
import etl.dispatch.java.ods.dao.DimIpDao;
import etl.dispatch.java.ods.domain.DimIp;
import etl.dispatch.java.ods.service.OdsDimIpService;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;

@Service
public class OdsDimIpServiceImpl implements OdsDimIpService {
	private final static Logger logger = LoggerFactory.getLogger(OdsDimIpServiceImpl.class);
	// 设置并发数为10，即同一时间最多只能有10个线程往cache执行写入操作
	private final static int DEFAULT_CACHE_CONCURRENCYLEVEL = 8;
	// 设置cache的初始大小为10，要合理设置该值
	private final static int DEFAULT_CACHE_INITIAL_CAPACITY = 1024;
	// Guava 缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
	private final static int DEFAULT_CACHE_MAX_SIZE = 40960;
	// Guava 缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
	private final static long DEFAULT_CACHE_EXPIRE = 7200;
	private final static DimIp NULL_NOT_PRESENT = new DimIp(); 
	
	@Autowired
	private DimDataSource dimDataSource;
	
	@Autowired
	private DimIpDao dimIpDao;
	private LoadingCache<DimIpKey, DimIp> dimIpKeyCache;
	private Lock lock;
	
	public OdsDimIpServiceImpl() {
		this.dimIpKeyCache = CacheBuilder.newBuilder()
				                .concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                .build(new DimIpKeyCacheLoader(this));
		this.lock = new ReentrantLock();
	}

	@Override
	public DimIp getDimIpByDimIp(DimIp ip, boolean createIfNotExist) {
		if (null == ip) {
			return null;
		}
		DimIp dimIp = null;
		try {
			DimIpKey dimIpKey = new DimIpKey(ip.getIp(), ip.getIpNum());
			dimIp = dimIpKeyCache.get(dimIpKey);
			// 缓存中没有，从数据库中加载
			if (dimIp == null || dimIp == NULL_NOT_PRESENT) {
				this.lock.lock();
				try {
					dimIp = this.findDimIpByIp(ip.getIp(), ip.getIpNum());
					if (null == dimIp) {
						if (createIfNotExist) {
							this.saveDimIp(ip);
						}
					}
					dimIp = this.findDimIpByIp(ip.getIp(), ip.getIpNum());
					this.dimIpKeyCache.put(dimIpKey, dimIp);
				} finally {
					this.lock.unlock();
				}
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimIpInfo by dimIpKeyCache from cache: " + t.getMessage(), t);
		}
		return dimIp == null ? null : dimIp;
	}

	@Override
	public DimIp getDimIpByIp(String ip, long ipNum) {
		if (StringUtil.isNullOrEmpty(ip)) {
			return null;
		}
		DimIp dimIp = null;
		try {
			DimIpKey dimIpKey = new DimIpKey(ip, ipNum);
			dimIp = dimIpKeyCache.get(dimIpKey);
			if (dimIp == null || dimIp == NULL_NOT_PRESENT) {
				dimIp = this.findDimIpByIp(ip, ipNum);
				if (null != dimIp) {
					this.dimIpKeyCache.put(dimIpKey, dimIp);
				}
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIpInfo by dimIpKeyCache from cache: " + e.getMessage(), e);
		} catch (Throwable t) {
			logger.error("unexpected error to load dimIpInfo by dimIpKeyCache from cache: " + t.getMessage(), t);
		}
		return dimIp == null ? null : dimIp;
	}


	@Override
	public DimIp findDimIpByIp(String ip, long ipNum) {
		if (StringUtil.isNullOrEmpty(ip)) {
			return null;
		}
		List<DimIp> rs = this.dimIpDao.findDimIpByIp(dimDataSource, new NewMapUtil("ip", ip).set("ipNum", ipNum).get());
		return rs == null || rs.size() == 0 ? null : rs.get(0);
	}
	
	@Override
	public void saveDimIp(DimIp dimIp) {
		this.dimIpDao.saveDimIp(dimDataSource,dimIp);
	}

	private static class DimIpKey {
		private String ip;
		private long ipNum;
		private int hash;

		public DimIpKey(String ip, long ipNum) {
			this.ip = ip;
			this.ipNum = ipNum;
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
			DimIpKey other = (DimIpKey) obj;
			if (ip == null) {
				if (other.ip != null)
					return false;
			} else if (!ip.equals(other.ip))
				return false;
			return true;
		}
	}

	private static class DimIpKeyCacheLoader extends CacheLoader<DimIpKey, DimIp> {
		private OdsDimIpService odsDimIpService;

		DimIpKeyCacheLoader(OdsDimIpService odsDimIpService) {
			this.odsDimIpService = odsDimIpService;
		}

		@Override
		public DimIp load(DimIpKey ipKey) throws Exception {
			DimIp dimIp = this.odsDimIpService.findDimIpByIp(ipKey.ip, ipKey.ipNum);
			return dimIp == null ? NULL_NOT_PRESENT : (DimIp) dimIp.clone();
		}
	}
}
