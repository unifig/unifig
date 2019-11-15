package etl.dispatch.gather.api.umeng.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.gather.api.umeng.base.UmengBase;
import etl.dispatch.gather.api.umeng.service.IUmengService;
import etl.dispatch.java.ods.domain.DimAppVersion;
import etl.dispatch.java.ods.service.OdsFullDimHolderService;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.StringUtil;

/**
 * 友盟获取事件列表
 * 
 *
 *
 */
@Service("events")
public class UmengEventsServiceImpl extends UmengBase implements IUmengService {
	private Map<String, String> parameterMap = new HashMap<String, String>();
	private Map<String, String> headerMap = new HashMap<String, String>();
	private List<Map> events;
	private static String EVENT_URL = "";
	private static String USERNAME = "";
	private static String PASSWORD = "";
	
	@Autowired
	private OdsFullDimHolderService dimHolderService;

	@Override
	public String getName() {
		return UmengEventsServiceImpl.class.getCanonicalName();
	}

	UmengEventsServiceImpl() {
		EVENT_URL = PropertiesHolder.getProperty("umeng.eventUrl");
		USERNAME = PropertiesHolder.getProperty("umeng.username");
		PASSWORD = PropertiesHolder.getProperty("umeng.password");
	}

	/**
	 * 查询所有事件列表
	 * 
	 * @author: ylc
	 * @throws Exception 
	 */
	public List<Map> getEvents(String token, String appKey , Integer platId , Integer appId) throws Exception {
		String eventUrl = EVENT_URL;
		String userName = USERNAME;
		String password = PASSWORD;
		headerMap = super.getAuthorizeHeader(userName, password);
		if (headerMap != null) {
			parameterMap.put("auth_token", token);
			parameterMap.put("appkey", appKey);
			parameterMap.put("page", "1");
			parameterMap.put("per_page", "100000");
			parameterMap.put("period_type", "daily");
			parameterMap.put("start_date", DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -2));
			parameterMap.put("end_date",  DateUtil.getSysStrCurrentDate("yyyy-MM-dd", -1));
			String response = super.doGet(eventUrl, headerMap, parameterMap);
			if (!StringUtil.isNullOrEmpty(response)) {
				events = (List<Map>) JSON.parse(response);
			}
		}
		// 根据platId查询相应的的versions,返回补充后的事件对象
		if (!StringUtil.isNullOrEmpty(platId)) {
			List<DimAppVersion> versions = dimHolderService.getAppVersionById(platId.shortValue(), appId.shortValue());
			events.forEach(a -> {
				//应用appKey
				a.put("appKey", appKey);
				//平台类型
				a.put("platId", platId);
				//应用ID
				a.put("appId", appId);
				//应用版本
				a.put("versions", versions);
			});
		} else {
			events.forEach(a -> {
				a.put("appKey", appKey);
				a.put("platId", null);
				a.put("appId", null);
				a.put("versions", null);
			});
		}
		return events == null ? new ArrayList<Map>() : events;
	}

}
