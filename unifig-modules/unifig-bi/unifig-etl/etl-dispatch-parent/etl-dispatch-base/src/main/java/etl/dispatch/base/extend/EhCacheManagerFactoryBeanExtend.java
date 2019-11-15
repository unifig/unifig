package etl.dispatch.base.extend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 用于扩展EhCacheManagerFactoryBean,支持从多个ehcache-config.xml文件中读取配置,方便模块化配置。
 * 
 *
 *
 */
@Service("ehCacheManagerFactory")
public class EhCacheManagerFactoryBeanExtend extends EhCacheManagerFactoryBean {
	private static Logger log = LoggerFactory.getLogger(EhCacheManagerFactoryBeanExtend.class);

	private static String cacheConfigPath = "classpath*:conf/ehcache/ehcache-*.xml";
	
	/**
	 * 把所有的缓存文件读取出来进行合并生成一个xml让缓存进行加载。<br/>
	 * 详细描述：根据配置的缓存文件通配，找到所有的缓存信息，进行合并后生成一个配置文件，调用父类的setConfigLocation加载此文件。<br/>
	 * 使用方式：cacheConfigPath的set方法，xml中配置的缓存文件路径，会自动调用。
	 * @param cacheConfigPath xml中配置的ehcache-*.xml的资源。
	 */
	@PostConstruct
	public void init() {
		try {
			Resource[] configLocation = this.getResources();
			if (null == configLocation || configLocation.length == 0) {
				return;
			}
			this.setLocalConfigPath(configLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Resource[] getResources() throws Exception {
		PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
		Assert.notNull(cacheConfigPath, cacheConfigPath + " path is null");
		Resource[] locations  = resolover.getResources(cacheConfigPath);
		return locations;
	}
	
	private void setLocalConfigPath(Resource[] cacheConfigPath) {
		XMLWriter xmlWriter = null;
		try {
			if (cacheConfigPath != null) {
				String tmpPath = System.getProperty("java.io.tmpdir");
				Document outputDoc = DocumentHelper.createDocument();
				Element rootEle = outputDoc.addElement("ehcache");
				File outputFile = new File(tmpPath, "ehcache-config_" + DateTime.now().toString("yyyyMMddHHmmss") + ".xml");
				Map<String, String> cacheNameMap = new HashMap<String, String>();
				for (Resource cacheConfigRes : cacheConfigPath) {
					SAXReader saxReader = new SAXReader();
					Document doc = saxReader.read(cacheConfigRes.getInputStream());
					Element ehcacheEle = doc.getRootElement();
					if (cacheConfigRes.getFilename().equalsIgnoreCase("cache-core.xml")) {
						Element diskStoreEle = ehcacheEle.element("diskStore");
						Element defaultCacheEle = ehcacheEle.element("defaultCache");
						if (diskStoreEle != null) {
							rootEle.add(diskStoreEle.createCopy());
						}
						if (defaultCacheEle != null) {
							rootEle.add(defaultCacheEle.createCopy());
						}
					}
					@SuppressWarnings("unchecked")
					List<Element> cacheEleList = ehcacheEle.elements("cache");
					for (Element cacheEle : cacheEleList) {
						String cacheName = cacheEle.attributeValue("name");
						if (cacheNameMap.keySet().contains(cacheName)) {
							log.error("ehcache cache配置已存在! cacheName=[{}],存在路径为:[{},{}]", new String[] { cacheName, cacheConfigRes.getURL().toString(), cacheNameMap.get(cacheName) });
						} else {
							rootEle.add(cacheEle.createCopy());
							cacheNameMap.put(cacheName, cacheConfigRes.getURL().toString());
						}
					}
				}
				xmlWriter = new XMLWriter(new FileWriter(outputFile));
				xmlWriter.write(outputDoc);
				xmlWriter.flush();
				log.info("ehcache 配置文件合并为:[{}]", outputFile.getAbsolutePath());
				super.setConfigLocation(new UrlResource(outputFile.toURI()));
			}
		} catch (Exception e) {
			log.error("ehcache 配置文件读取错误!" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (xmlWriter != null) {
				try {
					xmlWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}