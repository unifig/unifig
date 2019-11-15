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
public class EtlConfInfoTasksCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoTasksCache.class);
	private static final String cacheKey = "etl.dispatch.config.confInfoTasks";
	public static final String nodeKeyVal = "confInfoTasks";
	private static final String statusKey = "1";
	private static final Integer statusKeyInt = 1;

	@Autowired
	private IConfInfoTasksService confInfoTasksService;

	private static EtlConfInfoTasksCache etlConfigCache = new EtlConfInfoTasksCache();

	public static EtlConfInfoTasksCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfInfoTasksCache();
		}
		return etlConfigCache;
	}

	protected IConfInfoTasksService getConfInfoTasksService() {
		if (this.confInfoTasksService == null) {
			this.confInfoTasksService = SpringContextHolder.getBean("iConfInfoTasksService", IConfInfoTasksService.class);
		}
		return this.confInfoTasksService;
	}

	@Override
	public void initialize() {
		ConfInfoTasksEntity confInfoTasksEntity = new ConfInfoTasksEntity();
		confInfoTasksEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfInfoTasksService().findConfInfoTasks(confInfoTasksEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度任务配置资源";
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
		ConfInfoTasksEntity confInfoTasks = new ConfInfoTasksEntity();
		ObjectClassHelper.setFieldValue(confInfoTasks, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confInfoTasks, "status", statusKey);
		return this.getConfInfoTasksService().findConfInfoTasks(confInfoTasks);
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
