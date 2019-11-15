package etl.dispatch.base.holder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import etl.dispatch.util.PropertiesUtil;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.secret.EncryptUtil;



/**
 * 本地Properties属性
 *
 *
 */
public class PropertiesHolder {
	private static Logger logger = LoggerFactory.getLogger(PropertiesHolder.class);
	private static final String ENCRYPT_DES_KEY = "Perfect-baidu-assistant-@-QT-#$^&*956*(*)";
	private static Map<String, Object> appProperties = new HashMap<String, Object>();
	private static final String rootPackage = "conf/properties/";

	static {
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PropertiesHolder() {
	}

	public static void loadProperties() throws Exception {
		// Spring获取路径
		Resource[] resources = getResources();
		// 获取jar文件路径
		if (null == resources || resources.length <= 0) {
			return;
		}
		// 加载properties
		setLocations(resources);
	}

	private static Resource[] getResources() throws Exception {
		PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
		Assert.notNull(rootPackage, rootPackage + " path is null");
		try {
			boolean isTestEnvironment = isTestEnvironment();
			Resource[] resource = null;
			if (isTestEnvironment) {
				resource = resolover.getResources("classpath*:" + rootPackage + "local/*.properties");
			} else {
				resource = resolover.getResources("classpath*:" + rootPackage + "deploy/*.properties");
			}
			Resource[] locations = getRootResource();
			if (null == resource || null == locations) {
				return null;
			}
			List<Resource> resourceList = Arrays.asList(resource);
			List<Resource> locationsList = Arrays.asList(locations);
			List<Resource> allProperList = new ArrayList<Resource>();
			if (null != resourceList && !resourceList.isEmpty()) {
				allProperList.addAll(resourceList);
			}
			if (null != locationsList && !locationsList.isEmpty()) {
				allProperList.addAll(locationsList);
			}
			if (null != allProperList && !allProperList.isEmpty()) {
				Resource[] allResource = allProperList.toArray(new Resource[allProperList.size()]);
				return allResource;
			}
			return resource;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取通用路径
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Resource[] getRootResource() throws IOException {
		PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
		Assert.notNull(rootPackage,rootPackage+" path is null");
		Resource[] locations = resolover.getResources("classpath*:" + rootPackage + "*.properties");
		return locations;
	}

	/**
	 * 判断是否测试环境
	 * 
	 * @return
	 * @throws IOException
	 */
	private static boolean isTestEnvironment() throws IOException {
		Resource[] locations = getRootResource();
		if (null == locations || locations.length <= 0) {
			return true;
		}
		Properties tmpPro = new Properties();
		for (Resource res : locations) {
			try {
				tmpPro.load(new InputStreamReader(res.getInputStream(), "utf-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!tmpPro.isEmpty()) {
			String environment = tmpPro.getProperty("web.environment.istest");
			if (!StringUtil.isNullOrEmpty(environment)) {
				return Boolean.parseBoolean(environment);
			}
		}
		return true;
	}

	/**
	 * 详细描述：接收一个Resource类型的数组作为参数，将locations标签下值都会被解析成Resource，
	 * 而这个resource本身则包含了访问这个resource的方法，在这里resource代表的则是properties文件。
	 * 
	 * @param locations Resource类型的数组。
	 * @throws IOException
	 * @see #setLocations(Resource[])
	 */
	public static void setLocations(Resource[] locations) throws IOException {
		// 对properties文件进行排序,先读取jar包中的属性文件,jar包外的属性配置可覆盖Jar包中的属性配置
		Arrays.sort(locations, new Comparator<Resource>() {
			@Override
			public int compare(Resource rs1, Resource rs2) {
				try {
					if (ResourceUtils.JAR_FILE_EXTENSION.equals("." + rs1.getURL().getProtocol())) {
						return -1;
					} else {
						return 1;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
		Map<String, Object> tmpMap = new HashMap<>();
		// 加载Resource到Map
		for (Resource resource : locations) {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			Map<String, Object> propertiesMap = new HashMap<String, Object>((Map) properties);
			tmpMap.putAll(propertiesMap);
		}
		boolean isDecrypted = getIsDecryptedProp(tmpMap);
		for (Object key : tmpMap.keySet()) {
			String keyStr = key.toString();
			String value  = String.valueOf(tmpMap.get(keyStr));
			if (keyStr.endsWith(".encrypted")) {
				String newKey   = keyStr.substring(0, keyStr.lastIndexOf(".encrypted"));
				String newValue = value;
				if (isDecrypted) {
					newValue = PropertiesHolder.dencryptProperty(value);
				}
				appProperties.put(newKey, newValue);
			} else {
				appProperties.put(keyStr, value);
			}
		}
		logger.info("加载Properties配置文件:" + StringUtils.arrayToDelimitedString(locations, ","));
		System.out.println("加载Properties配置文件:" + StringUtils.arrayToDelimitedString(locations, ","));
	}

	/** 输出所有属性文件中配置的属性配置项 **/
	@SuppressWarnings({ "unused", "rawtypes" })
	private static void printAllProperties(Resource[] locations) {
		for (Resource res : locations) {
			PropertiesUtil propertiesUtil = new PropertiesUtil();
			try {
				propertiesUtil.load(new InputStreamReader(res.getInputStream(), "utf-8"));
				Enumeration enums = propertiesUtil.keys();
				while (enums.hasMoreElements()) {
					String propName = (String) enums.nextElement();
					System.out.println(res.getURL().getPath().substring(res.getURL().getPath().lastIndexOf("/") + 1) + "," + propName + "," + propertiesUtil.getProperty(propName));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取属性对象。<br/>
	 * 详细描述：通过属性key，获取属性对象。<br/>
	 * 使用方式：可在java代码中直接调用此静态方法。
	 * 
	 * @param key 属性key。
	 * @return 返回查询到的属性对象。
	 */
	public static Object getPropertyObj(String key) {
		if (appProperties == null) {
			logger.error("未初始化加载属性文件，请检查Spring配置");
			return null;
		}
		return appProperties.get(key);
	}

	/**
	 * 获取String类型属性值。<br/>
	 * 详细描述：通过属性key，获取属性值。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param key 属性key。
	 * @return 返回String类型的属性值。
	 */
	public static String getProperty(String key) {
		return String.valueOf(getPropertyObj(key));
	}

	/**
	 * 获取int类型属性值。<br/>
	 * 详细描述：通过属性key，获取属性值。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param key 属性key。
	 * @return 返回int类型的属性值。
	 */
	public static int getIntProperty(String key) {
		return Integer.valueOf(getProperty(key));
	}
	
	/**
	 * 获取long类型属性值。<br/>
	 * 详细描述：通过属性key，获取属性值。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param key 属性key。
	 * @return 返回int类型的属性值。
	 */
	public static long getLongProperty(String key) {
		return Long.valueOf(getProperty(key));
	}

	/**
	 * 获取boolean类型属性值。 <br/>
	 * 详细描述：通过属性key，获取属性值。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param key 属性key。
	 * @return 返回boolean类型的属性值。
	 */
	public static boolean getBooleanProperty(String key) {
		String val = getProperty(key);
		if (val != null && "true".equalsIgnoreCase(val)) {
			return true;
		}
		return false;
	}

	/**
	 * 从属性文件中获取是否解密属性文件。<br/>
	 * 
	 * @param props 属性文件。<br/>
	 * @return 返回boolean类型。
	 */
	private static boolean getIsDecryptedProp(Map<String, Object> propertiesMap) {
		String isDecrypted = String.valueOf(propertiesMap.get("core.global.isDecrypted"));
		if (isDecrypted != null && isDecrypted.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 加密属性。<br/>
	 * 详细描述：对属性进行加密。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param prop 要加密的属性。
	 * @return 返回加密后的属性。
	 */
	public static String EncryptProperty(String prop) {
		return EncryptUtil.encrypt(prop, PropertiesHolder.ENCRYPT_DES_KEY);
	}

	/**
	 * 解密属性。<br/>
	 * 详细描述：对加密的属性进行解密。<br/>
	 * 使用方式：java代码中可直接调用此静态方法。
	 * 
	 * @param prop 加密的属性。
	 * @return 返回解密后的属性。
	 */
	public static String dencryptProperty(String prop) {
		return EncryptUtil.decrypt(prop, PropertiesHolder.ENCRYPT_DES_KEY);
	}

	/**
	 * 根据前缀获取符合条件的所有properties属性值。
	 * 详细描述：根据前缀进行匹配，找出符合该前缀条件的所有的properties的属性值，以map返回。<br/>
	 * 使用方式：PropertiesHolder.getPropertiesByPrefix("前缀")方式调用。
	 * 
	 * @param prefix properties的key前缀。
	 * @return 符合参数条件的map。
	 */
	public static Map<String, Object> getPropertiesByPrefix(String prefix, boolean substr) {
		if (appProperties == null) {
			logger.error("未初始化加载属性文件，请检查Spring配置");
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> set = appProperties.keySet();
		for (String key : set) {
			if (key.startsWith(prefix)) {
				if(substr){
					resultMap.put(key.substring(prefix.length()), getPropertyObj(key));
				}else{
					resultMap.put(key, getPropertyObj(key));
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据前缀获取符合条件的所有properties属性值。
	 * 详细描述：根据前缀进行匹配，找出符合该前缀条件的所有的properties的属性值，以map返回。<br/>
	 * 使用方式：PropertiesHolder.getPropertiesByPrefix("前缀")方式调用。
	 * 
	 * @param prefix properties的key前缀。
	 * @return 符合参数条件的map。
	 */
	public static Map<String, Object> getPropertiesByPrefix(String prefix) {
		if (appProperties == null) {
			logger.error("未初始化加载属性文件，请检查Spring配置");
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> set = appProperties.keySet();
		for (String key : set) {
			if (key.startsWith(prefix)) {
				resultMap.put(key, getPropertyObj(key));
			}
		}
		return resultMap;
	}

	/**
	 * 根据前缀获取符合条件的所有properties属性值。
	 * 详细描述：根据前缀进行匹配，找出符合该前缀条件的所有的properties的属性值，以map返回。<br/>
	 * 使用方式：PropertiesHolder.getPropertiesBySuffix("后缀")方式调用。
	 * 
	 * @param Suffix properties的key后缀。
	 * @return 符合参数条件的map。
	 */
	public static Map<String, Object> getPropertiesBySuffix(String suffix, boolean substr) {
		if (appProperties == null) {
			logger.error("未初始化加载属性文件，请检查Spring配置");
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> set = appProperties.keySet();
		for (String key : set) {
			if (key.endsWith(suffix)) {
				if(substr){
					resultMap.put(key.substring(0, (key.length()-suffix.length())), getPropertyObj(key));
				}else{
					resultMap.put(key, getPropertyObj(key));
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据前缀获取符合条件的所有properties属性值。
	 * 详细描述：根据前缀进行匹配，找出符合该前缀条件的所有的properties的属性值，以map返回。<br/>
	 * 使用方式：PropertiesHolder.getPropertiesBySuffix("后缀")方式调用。
	 * 
	 * @param Suffix properties的key后缀。
	 * @return 符合参数条件的map。
	 */
	public static Map<String, Object> getPropertiesBySuffix(String suffix) {
		if (appProperties == null) {
			logger.error("未初始化加载属性文件，请检查Spring配置");
			return null;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> set = appProperties.keySet();
		for (String key : set) {
			if (key.endsWith(suffix)) {
				resultMap.put(key, getPropertyObj(key));
			}
		}
		return resultMap;
	}
	
	public static void main(String[] arges){
		System.out.println(PropertiesHolder.EncryptProperty("datacenter_2018"));
		System.out.println(PropertiesHolder.dencryptProperty("9499A16496200AE870A8011E30E9A1D212C9F3A782F8AEA2E044A40210556C3529E2028CAEB21D26D47CEA89D280CD6AE527F3CA7EB19E822198152D1FF3FBBBE5737BEC58E0B2346C23B3FF9403071C4CCE88D56CDB3DDA7F943F0F593B0F9E7BA5635D7E7C6AE3200F240AC825D634068950CFE81DACD84BDAB78ED9602E7C8FBC30CE0A4293EB4198B5C8A9F8D92C"));
	}
}