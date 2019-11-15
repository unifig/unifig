package etl.dispatch.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import etl.dispatch.base.holder.AbstractCacheHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.initialize.IWebInitializable;
import etl.dispatch.config.entity.ConfRelyGroupEntity;
import etl.dispatch.config.service.IConfRelyGroupService;
import etl.dispatch.util.helper.ObjectClassHelper;

@Component
public class EtlConfRelyGroupCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfRelyGroupCache.class);
	private static final String cacheKey = "etl.dispatch.config.confRelyGroup";
	public static final String nodeKeyVal = "confRelyGroup";
	private static final String statusKey = "1";
	private static final Integer statusKeyInt =1;

	@Autowired
	private IConfRelyGroupService confRelyGroupService;

	private static EtlConfRelyGroupCache etlConfigCache = new EtlConfRelyGroupCache();

	public static EtlConfRelyGroupCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfRelyGroupCache();
		}
		return etlConfigCache;
	}

	protected IConfRelyGroupService getConfRelyGroupService() {
		if (this.confRelyGroupService == null) {
			this.confRelyGroupService = SpringContextHolder.getBean("iConfRelyGroupService", IConfRelyGroupService.class);
		}
		return this.confRelyGroupService;
	}

	@Override
	public void initialize() {
		ConfRelyGroupEntity confRelyGroupEntity = new ConfRelyGroupEntity();
		confRelyGroupEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfRelyGroupService().findConfRelyGroup(confRelyGroupEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度任务组依赖配置资源";
	}

	/**
	 * 配置菜单资源缓存区key值
	 */
	@Override
	protected String setCacheKey() {
		return cacheKey;
	}

	@Override
	protected Object getDataBaseValue(String nodeKeyVal, String propertyName, String propertyValue) {
		// 设置对象的属性值
		ConfRelyGroupEntity confRelyGroup = new ConfRelyGroupEntity();
		ObjectClassHelper.setFieldValue(confRelyGroup, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confRelyGroup, "status", statusKey);
		return this.getConfRelyGroupService().findConfRelyGroup(confRelyGroup);
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
