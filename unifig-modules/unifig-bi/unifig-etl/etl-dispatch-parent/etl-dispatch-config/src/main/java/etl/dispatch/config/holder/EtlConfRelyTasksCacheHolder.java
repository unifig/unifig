package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfInfoTasksCache;
import etl.dispatch.config.cache.EtlConfRelyTasksCache;
import etl.dispatch.config.entity.ConfInfoTasksEntity;
import etl.dispatch.config.entity.ConfRelyTasksEntity;

import net.sf.ehcache.Element;

@Component
public class EtlConfRelyTasksCacheHolder  implements ScheduledService {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfRelyTasksCacheHolder.class);

	public static void refreshCache() {
		EtlConfRelyTasksCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfRelyTasksEntity> getAllEtlTasksRelys() {
		Element element = (Element) EtlConfRelyTasksCache.getInstance().getCacheAllValue(EtlConfRelyTasksCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfRelyTasksEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfRelyTasksEntity getEtlTasksRely(String propertyName, String propertyValue) {
		final List<ConfRelyTasksEntity> child = (List<ConfRelyTasksEntity>) EtlConfRelyTasksCache.getInstance().getCacheValue(EtlConfRelyTasksCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfRelyTasksEntity> getEtlTasksRelys(String propertyName, String propertyValue) {
		final List<ConfRelyTasksEntity> child = (List<ConfRelyTasksEntity>) EtlConfRelyTasksCache.getInstance().getCacheValue(EtlConfRelyTasksCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "加载ETL调度任务依赖配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
	}

}
