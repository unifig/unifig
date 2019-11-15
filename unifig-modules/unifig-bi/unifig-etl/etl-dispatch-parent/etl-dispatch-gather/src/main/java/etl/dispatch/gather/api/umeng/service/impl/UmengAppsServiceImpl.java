package etl.dispatch.gather.api.umeng.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.gather.api.umeng.base.UmengBase;
import etl.dispatch.gather.api.umeng.service.IUmengService;
import etl.dispatch.util.StringUtil;

/**
 * 友盟获取应用列表
 * 
 *
 *
 */
@Service("apps")
public class UmengAppsServiceImpl extends UmengBase implements IUmengService {
	private Map<String, String> parameterMap = new HashMap<String, String>();
	List<Map> appList;
	private static String APP_URL = "";
	private static String USERNAME = "";
	private static String PASSWORD = "";
	//private UmengAppRelationImpl umengAppRelationImpl;

	public UmengAppsServiceImpl() {
		APP_URL = PropertiesHolder.getProperty("umeng.appsUrl");
		USERNAME = PropertiesHolder.getProperty("umeng.username");
		PASSWORD = PropertiesHolder.getProperty("umeng.password");
	}

	@Override
	public String getName() {
		return "UmengApps";
	}

	/**
	 * App列表
	 * 
	 * @author: ylc
	 * @throws Exception 
	 */
	public List<Map> getApps(String token, Map<String, String> headerMap) throws Exception {
		// 1: 获取友盟的平台集合
		String appUrl = APP_URL;
		String email = USERNAME;
		String password = PASSWORD;
		List<Map> apps = new ArrayList<>();
		if (headerMap != null) {
			parameterMap.put("auth_token", token);
			parameterMap.put("page", "1");
			parameterMap.put("per_page", "100000");
			String response = super.doGet(appUrl, headerMap, parameterMap);
			if (!StringUtil.isNullOrEmpty(response)){
				apps = JSON.parseObject(response, List.class);
			}
		}
		String iosproperty = PropertiesHolder.getProperty("umeng.appkeys.ios");
		String androidproperty = PropertiesHolder.getProperty("umeng.appkeys.android");
		// 4：筛选出有用的平台
		appList = apps.stream().filter(t -> {
			Boolean flag = false;
			if (iosproperty.split(":")[0].equals(t.get("appkey"))) {
				flag = true;
			}
			if (androidproperty.split(":")[0].equals(t.get("appkey"))) {
				flag = true;
			}
			return flag;
		}).peek(t -> {
			if (iosproperty.split(":")[0].equals(t.get("appkey"))) {
				t.put("appId", iosproperty.split(":")[1]);
				t.put("platId", iosproperty.split(":")[2]);
			}
			if (androidproperty.split(":")[0].equals(t.get("appkey"))) {
				t.put("appId", androidproperty.split(":")[1]);
				t.put("platId", androidproperty.split(":")[2]);
			}
		}).collect(Collectors.toList());
		return appList == null ? new ArrayList<Map>() : appList;
	}
	
	

}
