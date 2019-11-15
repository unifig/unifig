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
public class EtlConfInfoPythonScriptCache extends AbstractCacheHolder {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoPythonScriptCache.class);
	private static final String cacheKey = "etl.dispatch.config.confInfoPythonScript";
	public static final String nodeKeyVal = "confInfoPythonScript";
    private static final String statusKey = "1";
    private static final Integer statusKeyInt =1;

	@Autowired
	private IConfInfoPythonScriptService confInfoPythonScriptService;

	private static EtlConfInfoPythonScriptCache etlConfigCache = new EtlConfInfoPythonScriptCache();

	public static EtlConfInfoPythonScriptCache getInstance() {
		if (etlConfigCache == null) {
			etlConfigCache = new EtlConfInfoPythonScriptCache();
		}
		return etlConfigCache;
	}

	protected IConfInfoPythonScriptService getConfInfoPythonScriptService() {
		if (this.confInfoPythonScriptService == null) {
			this.confInfoPythonScriptService = SpringContextHolder.getBean("iConfInfoPythonScriptService", IConfInfoPythonScriptService.class);
		}
		return this.confInfoPythonScriptService;
	}

	@Override
	public void initialize() {
		ConfInfoPythonScriptEntity confInfoPythonScriptEntity = new ConfInfoPythonScriptEntity();
		confInfoPythonScriptEntity.setStatus(statusKeyInt);
		this.saveOrUpdateCacheValue(nodeKeyVal, this.getConfInfoPythonScriptService().findConfInfoPythonScript(confInfoPythonScriptEntity));
	}

	@Override
	public String cacheDesc() {
		return "加载ETL调度Python脚本配置资源";
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
		ConfInfoPythonScriptEntity confInfoPythonScript = new ConfInfoPythonScriptEntity();
		ObjectClassHelper.setFieldValue(confInfoPythonScript, propertyName, propertyValue);
		ObjectClassHelper.setFieldValue(confInfoPythonScript, "status", statusKey);
		return this.getConfInfoPythonScriptService().findConfInfoPythonScript(confInfoPythonScript);
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
