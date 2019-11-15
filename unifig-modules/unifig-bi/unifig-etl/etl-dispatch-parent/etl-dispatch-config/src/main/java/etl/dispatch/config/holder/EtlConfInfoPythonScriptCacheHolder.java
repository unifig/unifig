package etl.dispatch.config.holder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import etl.dispatch.base.scheduled.ScheduledService;
import etl.dispatch.config.cache.EtlConfInfoJavaScriptCache;
import etl.dispatch.config.cache.EtlConfInfoPythonScriptCache;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;

import net.sf.ehcache.Element;

@Component
public class EtlConfInfoPythonScriptCacheHolder   implements ScheduledService{
	private static final Logger logger = LoggerFactory.getLogger(EtlConfInfoPythonScriptCacheHolder.class);

	public static void refreshCache() {
		EtlConfInfoPythonScriptCache.getInstance().initialize();
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoPythonScriptEntity> getAllEtlPythonScriptInfos() {
		Element element = (Element) EtlConfInfoPythonScriptCache.getInstance().getCacheAllValue(EtlConfInfoPythonScriptCache.nodeKeyVal);
		if (element != null) {
			return (List<ConfInfoPythonScriptEntity>) element.getObjectValue();
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static ConfInfoPythonScriptEntity getEtlPythonScriptInfo(String propertyName, String propertyValue) {
		final List<ConfInfoPythonScriptEntity> child = (List<ConfInfoPythonScriptEntity>) EtlConfInfoPythonScriptCache.getInstance().getCacheValue(EtlConfInfoPythonScriptCache.nodeKeyVal, propertyName, propertyValue);
		// child != null
		if (child.size() > 0) {
			return child.get(0);
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static List<ConfInfoPythonScriptEntity> getEtlPythonScriptInfos(String propertyName, String propertyValue) {
		final List<ConfInfoPythonScriptEntity> child = (List<ConfInfoPythonScriptEntity>) EtlConfInfoPythonScriptCache.getInstance().getCacheValue(EtlConfInfoPythonScriptCache.nodeKeyVal, propertyName, propertyValue);
		if (child != null) {
			return child;
		} else
			return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "加载ETL调度Python脚本配置资源";
	}

	@Override
	public void schedule() {
		refreshCache();
	}

}
