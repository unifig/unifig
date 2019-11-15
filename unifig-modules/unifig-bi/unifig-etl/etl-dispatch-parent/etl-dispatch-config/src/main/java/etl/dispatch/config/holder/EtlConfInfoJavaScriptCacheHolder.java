package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfInfoGroupCache;
import etl.dispatch.config.cache.EtlConfInfoJavaScriptCache;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;

import net.sf.ehcache.Element;

@Component
public class EtlConfInfoJavaScriptCacheHolder  implements ScheduledService{
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoJavaScriptCacheHolder.class);

	public static void refreshCache() {
		EtlConfInfoJavaScriptCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoJavaScriptEntity> getAllEtlJavaScriptInfos() {
		Element element = (Element) EtlConfInfoJavaScriptCache.getInstance().getCacheAllValue(EtlConfInfoJavaScriptCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfInfoJavaScriptEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfInfoJavaScriptEntity getEtlJavaScriptInfo(String propertyName, String propertyValue) {
		final List<ConfInfoJavaScriptEntity> child = (List<ConfInfoJavaScriptEntity>) EtlConfInfoJavaScriptCache.getInstance().getCacheValue(EtlConfInfoJavaScriptCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoJavaScriptEntity> getEtlJavaScriptInfos(String propertyName, String propertyValue) {
		final List<ConfInfoJavaScriptEntity> child = (List<ConfInfoJavaScriptEntity>) EtlConfInfoJavaScriptCache.getInstance().getCacheValue(EtlConfInfoJavaScriptCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		return "加载ETL调度Java脚本配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
	}

}
