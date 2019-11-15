package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfInfoPythonScriptCache;
import etl.dispatch.config.cache.EtlConfInfoTasksCache;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;
import etl.dispatch.config.entity.ConfInfoTasksEntity;

import net.sf.ehcache.Element;

@Component
public class EtlConfInfoTasksCacheHolder implements ScheduledService{
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoTasksCacheHolder.class);

	public static void refreshCache() {
		EtlConfInfoTasksCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoTasksEntity> getAllEtlTasksInfos() {
		Element element = (Element) EtlConfInfoTasksCache.getInstance().getCacheAllValue(EtlConfInfoTasksCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfInfoTasksEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfInfoTasksEntity getEtlTasksInfo(String propertyName, String propertyValue) {
		final List<ConfInfoTasksEntity> child = (List<ConfInfoTasksEntity>) EtlConfInfoTasksCache.getInstance().getCacheValue(EtlConfInfoTasksCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoTasksEntity> getEtlTasksInfos(String propertyName, String propertyValue) {
		final List<ConfInfoTasksEntity> child = (List<ConfInfoTasksEntity>) EtlConfInfoTasksCache.getInstance().getCacheValue(EtlConfInfoTasksCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		return "加载ETL调度任务配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
	}

}
