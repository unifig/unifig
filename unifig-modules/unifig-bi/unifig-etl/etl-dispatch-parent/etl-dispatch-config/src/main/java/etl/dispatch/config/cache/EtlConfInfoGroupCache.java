package etl.dispatch.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import etl.dispatch.base.holder.AbstractCacheHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.initialize.IWebInitializable;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.service.IConfInfoGroupService;
import etl.dispatch.util.helper.ObjectClassHelper;

@Component
public class EtlConfInfoGroupCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoGroupCache.class);
	private static final String cacheKey = "etl.dispatch.config.confInfoGroup";
	public static final String nodeKeyVal = "confInfoGroup";
    private static final String statusKey = "1";
	private static final Integer statusKeyInt = 1;
	@Autowired
	private IConfInfoGroupService confInfoGroupService;

	private static EtlConfInfoGroupCache etlConfigCache = new EtlConfInfoGroupCache();

	public static EtlConfInfoGroupCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfInfoGroupCache();
		}
		return etlConfigCache;
	}

	protected IConfInfoGroupService getConfInfoGroupService() {
		if (this.confInfoGroupService == null) {
			this.confInfoGroupService = SpringContextHolder.getBean("iConfInfoGroupService", IConfInfoGroupService.class);
		}
		return this.confInfoGroupService;
	}


	@Override
	public void initialize() {
		ConfInfoGroupEntity confInfoGroupEntity = new ConfInfoGroupEntity();
		confInfoGroupEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfInfoGroupService().findConfInfoGroup(confInfoGroupEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度分组数据配置资源";
	}

	/**
	 * 配置菜单资源缓存区key值
	 */
	@Override
	protected String setCacheKey() {
		return cacheKey;
	}

	/**
	 * 缓存Cache不存在，查询数据库
	 */
	@Override
	protected Object getDataBaseValue(String nodeKeyVal, String propertyName, String propertyValue) {
		// 设置对象的属性值
		ConfInfoGroupEntity confInfoGroup = new ConfInfoGroupEntity();
		ObjectClassHelper.setFieldValue(confInfoGroup, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confInfoGroup, "status", statusKey);
		return this.getConfInfoGroupService().findConfInfoGroup(confInfoGroup);
	}

	@Override
	protected void saveOrUpdateCacheValue(String nodeKeyVal, Object obj) {
		super.addOrUpdateCacheValue(nodeKeyVal, obj);
	}

	@Override
	protected void clearCacheValue(String nodeKeyVal) {
		super.removeCacheElement(nodeKeyVal);
	}

	@Override
	public Class<? extends IWebInitializable> setInitDepend() {
		return IWebInitializable.class;
	}
}
