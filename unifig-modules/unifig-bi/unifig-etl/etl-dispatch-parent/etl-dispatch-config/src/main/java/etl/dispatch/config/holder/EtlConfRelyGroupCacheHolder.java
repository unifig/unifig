package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfRelyGroupCache;
import etl.dispatch.config.entity.ConfRelyGroupEntity;

import net.sf.ehcache.Element;

@Component
public class EtlConfRelyGroupCacheHolder implements ScheduledService{
	private static final Logger logger = LoggerFactory.getLogger(EtlConfRelyGroupCacheHolder.class);

	public static void refreshCache() {
		EtlConfRelyGroupCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfRelyGroupEntity> getAllEtlGroupRelys() {
		Element element = (Element) EtlConfRelyGroupCache.getInstance().getCacheAllValue(EtlConfRelyGroupCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfRelyGroupEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfRelyGroupEntity getEtlGroupRely(String propertyName, String propertyValue) {
		final List<ConfRelyGroupEntity> child = (List<ConfRelyGroupEntity>) EtlConfRelyGroupCache.getInstance().getCacheValue(EtlConfRelyGroupCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfRelyGroupEntity> getEtlGroupRelys(String propertyName, String propertyValue) {
		final List<ConfRelyGroupEntity> child = (List<ConfRelyGroupEntity>) EtlConfRelyGroupCache.getInstance().getCacheValue(EtlConfRelyGroupCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "加载ETL调度任务组依赖配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
	}

}
