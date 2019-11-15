package etl.dispatch.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import etl.dispatch.base.holder.AbstractCacheHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.initialize.IWebInitializable;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;
import etl.dispatch.config.entity.ConfInfoTasksEntity;
import etl.dispatch.config.entity.ConfRelyTasksEntity;
import etl.dispatch.config.service.IConfInfoGroupService;
import etl.dispatch.config.service.IConfInfoJavaScriptService;
import etl.dispatch.config.service.IConfInfoPythonScriptService;
import etl.dispatch.config.service.IConfInfoTasksService;
import etl.dispatch.config.service.IConfRelyTasksService;
import etl.dispatch.util.helper.ObjectClassHelper;

@Component
public class EtlConfRelyTasksCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfRelyTasksCache.class);
	private static final String cacheKey = "etl.dispatch.config.confRelyTasks";
	public static final String nodeKeyVal = "confRelyTasks";
	private static final String statusKey = "1";
	private static final Integer statusKeyInt = 1;
	

	@Autowired
	private IConfRelyTasksService confRelyTasksService;

	private static EtlConfRelyTasksCache etlConfigCache = new EtlConfRelyTasksCache();

	public static EtlConfRelyTasksCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfRelyTasksCache();
		}
		return etlConfigCache;
	}

	protected IConfRelyTasksService getConfRelyTasksService() {
		if (this.confRelyTasksService == null) {
			this.confRelyTasksService = SpringContextHolder.getBean("iConfRelyTasksService", IConfRelyTasksService.class);
		}
		return this.confRelyTasksService;
	}

	@Override
	public void initialize() {
		ConfRelyTasksEntity confRelyTasksEntity = new ConfRelyTasksEntity();
		confRelyTasksEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfRelyTasksService().findConfRelyTasks(confRelyTasksEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度任务依赖配置资源";
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
		ConfRelyTasksEntity confRelyTasks = new ConfRelyTasksEntity();
		ObjectClassHelper.setFieldValue(confRelyTasks, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confRelyTasks, "status", statusKey);
		return this.getConfRelyTasksService().findConfRelyTasks(confRelyTasks);
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
