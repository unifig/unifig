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
public class EtlConfInfoJavaScriptCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoJavaScriptCache.class);
	private static final String cacheKey = "etl.dispatch.config.confInfoJavaScript";
	public static final String nodeKeyVal = "confInfoJavaScript";
	private static final String statusKey = "1";
	private static final Integer statusKeyInt = 1;

	@Autowired
	private IConfInfoJavaScriptService confInfoJavaScriptService;

	private static EtlConfInfoJavaScriptCache etlConfigCache = new EtlConfInfoJavaScriptCache();

	public static EtlConfInfoJavaScriptCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfInfoJavaScriptCache();
		}
		return etlConfigCache;
	}

	protected IConfInfoJavaScriptService getConfInfoJavaScriptService() {
		if (this.confInfoJavaScriptService == null) {
			this.confInfoJavaScriptService = SpringContextHolder.getBean("iConfInfoJavaScriptService", IConfInfoJavaScriptService.class);
		}
		return this.confInfoJavaScriptService;
	}

	@Override
	public void initialize() {
		ConfInfoJavaScriptEntity confInfoJavaScriptEntity = new ConfInfoJavaScriptEntity();
		confInfoJavaScriptEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfInfoJavaScriptService().findConfInfoJavaScript(confInfoJavaScriptEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度Java脚本配置资源";
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
		ConfInfoJavaScriptEntity confInfoJavaScript = new ConfInfoJavaScriptEntity();
		ObjectClassHelper.setFieldValue(confInfoJavaScript, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confInfoJavaScript, "status", statusKey);
		return this.getConfInfoJavaScriptService().findConfInfoJavaScript(confInfoJavaScript);
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
