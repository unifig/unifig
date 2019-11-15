package etl.dispatch.java.spap.ods.service.impl;

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
import etl.dispatch.java.spap.ods.dao.SpapDimNetWorkDao;
import etl.dispatch.java.spap.ods.domain.SpapDimNetWork;
import etl.dispatch.java.spap.ods.service.SpapOdsDimNetWorkService;
import etl.dispatch.util.StringUtil;
@Service
public class SpapOdsDimNetWorkServiceImpl implements SpapOdsDimNetWorkService , GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimNetWorkServiceImpl.class);
	// 网络类型维表字典转换; dim_network
	private LoadingCache<DimNetWorkKey, SpapDimNetWork> dimNetWorkKeyCache;
	private final static SpapDimNetWork DEFAULT_NOT_PRESENT = new SpapDimNetWork();
	@Autowired
	private SpapDimDataSource dimDataSource;
	@Autowired
	private SpapDimNetWorkDao dimNetWorkDao;
	
	public SpapOdsDimNetWorkServiceImpl(){
		//网络类型Guave緩存
		this.dimNetWorkKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimNetWorkKeyCacheLoader(this));
	}
	
	@Override
	public SpapDimNetWork getNetWorkByName(String netWorkName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(netWorkName)) {
			return null;
		}
		if (netWorkName.length() > DEFAULT_NAME_MAX_LENGTH) {
			netWorkName = netWorkName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimNetWork dimNetWork = null;
		try {
			DimNetWorkKey dimNetWorkKey = new DimNetWorkKey(netWorkName);
			if (createIfNotExist) {
				dimNetWork = this.dimNetWorkKeyCache.get(dimNetWorkKey);
			} else {
				dimNetWork = this.dimNetWorkKeyCache.getIfPresent(dimNetWorkKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		}
		return dimNetWork == null ? DEFAULT_NOT_PRESENT : dimNetWork;
	}

	@Override
	public SpapDimNetWork findNetWorkByName(String netWorkName) {
		return this.dimNetWorkDao.findNetWorkByName(dimDataSource, netWorkName);
	}

	@Override
	public void saveDimNetWork(SpapDimNetWork newNetWork) {
		this.dimNetWorkDao.saveDimNetWork(dimDataSource, newNetWork);
	}
	
	/**
	 * Guave网络类型维表Key
	 *
	 */
	private static class DimNetWorkKey {
		private String name;
		private String nameLowercase;

		public DimNetWorkKey(String name) {
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
			DimNetWorkKey other = (DimNetWorkKey) obj;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}
	
	/**
	 * 网络类型Guave
	 *
	 */
	private static class DimNetWorkKeyCacheLoader extends CacheLoader<DimNetWorkKey, SpapDimNetWork> {
		private SpapOdsDimNetWorkService odsDimNetWorkService;
		private Lock lock;

		public DimNetWorkKeyCacheLoader(SpapOdsDimNetWorkService odsDimNetWorkService) {
			this.odsDimNetWorkService = odsDimNetWorkService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimNetWork load(DimNetWorkKey dimNetWorkKey) throws Exception {
			SpapDimNetWork dimNetWork = odsDimNetWorkService.findNetWorkByName(dimNetWorkKey.name);
			if (dimNetWork == null) {
				this.lock.lock();
				try {
					dimNetWork = this.odsDimNetWorkService.findNetWorkByName(dimNetWorkKey.name);
					if (dimNetWork == null) {
						this.odsDimNetWorkService.saveDimNetWork(new SpapDimNetWork((short) 0, dimNetWorkKey.name));
						dimNetWork = this.odsDimNetWorkService.findNetWorkByName(dimNetWorkKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimNetWork == null ? DEFAULT_NOT_PRESENT : (SpapDimNetWork) dimNetWork.clone();
		}
	}

}
