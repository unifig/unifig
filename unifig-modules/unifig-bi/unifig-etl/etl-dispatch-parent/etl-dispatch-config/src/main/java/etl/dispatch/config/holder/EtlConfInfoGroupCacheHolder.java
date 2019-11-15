package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Element;
import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfInfoGroupCache;
import etl.dispatch.config.entity.ConfInfoGroupEntity;

@Component
public class EtlConfInfoGroupCacheHolder implements ScheduledService {
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoGroupCacheHolder.class);

	public static void refreshCache() {
		EtlConfInfoGroupCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoGroupEntity> getAllEtlGroupInfos() {
		Element element = (Element) EtlConfInfoGroupCache.getInstance().getCacheAllValue(EtlConfInfoGroupCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfInfoGroupEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfInfoGroupEntity getEtlGroupInfo(String propertyName, String propertyValue) {
		final List<ConfInfoGroupEntity> child = (List<ConfInfoGroupEntity>) EtlConfInfoGroupCache.getInstance().getCacheValue(EtlConfInfoGroupCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoGroupEntity> getEtlGroupInfos(String propertyName, String propertyValue) {
		final List<ConfInfoGroupEntity> child = (List<ConfInfoGroupEntity>) EtlConfInfoGroupCache.getInstance().getCacheValue(EtlConfInfoGroupCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		return "加载ETL调度分组数据配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
		
	}

}
