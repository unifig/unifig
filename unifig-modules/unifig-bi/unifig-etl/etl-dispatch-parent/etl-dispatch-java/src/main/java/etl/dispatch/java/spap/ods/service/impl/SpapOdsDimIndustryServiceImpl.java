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
import etl.dispatch.java.spap.ods.dao.SpapDimIndustryDao;
import etl.dispatch.java.spap.ods.domain.SpapDimIndustry;
import etl.dispatch.java.spap.ods.service.SpapOdsDimIndustryService;
import etl.dispatch.util.StringUtil;

@Service
public class SpapOdsDimIndustryServiceImpl implements SpapOdsDimIndustryService, GuavaCacheSpap {
	private static Logger logger = LoggerFactory.getLogger(SpapOdsDimIndustryServiceImpl.class);
	// 工作行业维表字典转换; dim_industry
	private LoadingCache<DimIndustryKey, SpapDimIndustry> dimIndustryKeyCache;
	private final static SpapDimIndustry INDUSTRY_NOT_PRESENT = new SpapDimIndustry();

	@Autowired
	private SpapDimDataSource dimDataSource;
	
	@Autowired
	private SpapDimIndustryDao dimIndustryDao;
	
	public SpapOdsDimIndustryServiceImpl() {
		//工作行业Guave緩存
		this.dimIndustryKeyCache = CacheBuilder.newBuilder().concurrencyLevel(DEFAULT_CACHE_CONCURRENCYLEVEL)
				                       .initialCapacity(DEFAULT_CACHE_INITIAL_CAPACITY)
				                       .maximumSize(DEFAULT_CACHE_MAX_SIZE)
				                       .expireAfterAccess(DEFAULT_CACHE_EXPIRE, TimeUnit.SECONDS)
				                       .build(new DimIndustryKeyCacheLoader(this));
	}

	/**
	 * Guave查詢工作行业
	 */
	@Override
	public SpapDimIndustry getIndustryByName(String industryName, boolean createIfNotExist) {
		if (StringUtil.isNullOrEmpty(industryName)) {
			return null;
		}
		if (industryName.length() > DEFAULT_NAME_MAX_LENGTH) {
			industryName = industryName.substring(0, DEFAULT_NAME_MAX_LENGTH);
		}
		SpapDimIndustry dimIndustry = null;
		try {
			DimIndustryKey dimIndustryKey = new DimIndustryKey(industryName);
			if (createIfNotExist) {
				dimIndustry = this.dimIndustryKeyCache.get(dimIndustryKey);
			} else {
				dimIndustry = this.dimIndustryKeyCache.getIfPresent(dimIndustryKey);
			}
		} catch (ExecutionException e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		} catch (UncheckedExecutionException e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		} catch (ExecutionError e) {
			logger.error("error to load dimIndustry from cache: " + e.getMessage(), e);
		}
		return dimIndustry == null ? INDUSTRY_NOT_PRESENT : dimIndustry;
	}

	/**
	 * 數據庫查詢工作行业
	 */
	@Override
	public SpapDimIndustry findIndustryByName(String industryName) {
		return this.dimIndustryDao.findIndustryByName(dimDataSource, industryName);
	}

	/**
	 * 保存工作行业
	 */
	@Override
	public void saveDimIndustry(SpapDimIndustry newIndustry) {
		this.dimIndustryDao.saveDimIndustry(dimDataSource, newIndustry);
	}
	
	/**
	 * Guave行业划分维表Key
	 *
	 */
	private static class DimIndustryKey {
		private String name;
		private String nameLowercase;

		public DimIndustryKey(String name) {
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
			DimIndustryKey other = (DimIndustryKey) obj;
			if (nameLowercase == null) {
				if (other.nameLowercase != null)
					return false;
			} else if (!nameLowercase.equals(other.nameLowercase))
				return false;
			return true;
		}
	}

	/**
	 * 工作行业Guave
	 *
	 */
	private static class DimIndustryKeyCacheLoader extends CacheLoader<DimIndustryKey, SpapDimIndustry> {
		private SpapOdsDimIndustryService odsDimIndustryService;
		private Lock lock;

		public DimIndustryKeyCacheLoader(SpapOdsDimIndustryService odsDimIndustryService) {
			this.odsDimIndustryService = odsDimIndustryService;
			this.lock = new ReentrantLock();
		}

		@Override
		public SpapDimIndustry load(DimIndustryKey dimIndustryKey) throws Exception {
			SpapDimIndustry dimIndustry = odsDimIndustryService.findIndustryByName(dimIndustryKey.name);
			if (dimIndustry == null) {
				this.lock.lock();
				try {
					dimIndustry = this.odsDimIndustryService.findIndustryByName(dimIndustryKey.name);
					if (dimIndustry == null) {
						this.odsDimIndustryService.saveDimIndustry(new SpapDimIndustry((short) 0, dimIndustryKey.name));
						dimIndustry = this.odsDimIndustryService.findIndustryByName(dimIndustryKey.name);
					}
				} finally {
					this.lock.unlock();
				}
			}
			return dimIndustry == null ? INDUSTRY_NOT_PRESENT : (SpapDimIndustry) dimIndustry.clone();
		}
	}

}
