package etl.dispatch.gather.api.umeng.base;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import etl.dispatch.util.DateUtil;
import etl.dispatch.util.HttpRequestor;
import etl.dispatch.util.StringUtil;

@Service
public class UmengBase {
	private HttpRequestor httpRequestor;

	/**
	 * post请求
	 * 
	 * @author: ylc
	 */
	public String doPost(String url, Map parameterMap) {
		String postString = "";
		try {
			httpRequestor = new HttpRequestor();
			postString = httpRequestor.doPost(url, parameterMap, null);
			if (!StringUtil.isNullOrEmpty(postString)) {
				return postString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return postString;
	}

	/**
	 * get请求
	 * 
	 * @author: ylc
	 * @throws Exception 
	 */
	public String doGet(String url, Map<String, String> headerMap, Map<String, String> parameterMap) throws Exception {
		String getString = "";
		StringBuffer dourl = new StringBuffer(url);
		
			if (parameterMap != null) {
				int i = 0;
				for (String key : parameterMap.keySet()) {
					if (i == 0) {
						dourl.append("?");
					}
					dourl.append(key + "=" + parameterMap.get(key) + "&");
					i++;
				}
			}
			httpRequestor = new HttpRequestor();
			getString = httpRequestor.doGet(dourl.toString(), headerMap);
			if (!StringUtil.isNullOrEmpty(getString)) {
				return getString;
			}
		
		return getString;
	}

	/**
	 * HTTP基本认证
	 * 
	 * @author: ylc
	 */
	public Map<String, String> getAuthorizeHeader(String user, String password) {
		if (StringUtil.isNullOrEmpty(user)) {
			return null;
		}
		if (StringUtil.isNullOrEmpty(password)) {
			return null;
		}
		Map<String, String> headerMap = new HashMap<>();
		String header = user + ":" + password;
		Base64.Encoder encoder = Base64.getEncoder();
		String encode = encoder.encodeToString(header.getBytes());
		headerMap.put("Authorization", "Basic" + encode);
		return headerMap;
	}
	
	/**
	 * 友盟请求失败发送邮件
	 * @author: ylc
	 */
	public void sendMail(String receiver, String email, Map<String, Object> message) {
		try {
			// 发送邮件告警: message
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("receiver", receiver);
			parameterMap.put("subject", DateUtil.getSysStrCurrentDate("yyyy-MM-dd") + "拉取错误");
			parameterMap.put("message", message.get("err"));
			new HttpRequestor().doPost(email, parameterMap, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
