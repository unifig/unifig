package etl.dispatch.gather.api.umeng.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.gather.api.umeng.base.UmengBase;
import etl.dispatch.gather.api.umeng.entity.Authorize;
import etl.dispatch.gather.api.umeng.service.IUmengService;
import etl.dispatch.util.StringUtil;

/**
 * 友盟权限认证
 * 
 *
 *
 */
@Service("authorize")
public class UmengAuthorizeServiceImpl extends UmengBase implements IUmengService {
	private Map<String, Object> parameterMap = new HashMap<String, Object>();
	private Authorize authorize;
	private static String TOKEN_URL = "";
	private static String USERNAME = "";
	private static String PASSWORD = "";

	UmengAuthorizeServiceImpl() {
		TOKEN_URL = PropertiesHolder.getProperty("umeng.tokenUrl");
		USERNAME = PropertiesHolder.getProperty("umeng.username");
		PASSWORD = PropertiesHolder.getProperty("umeng.password");
	}

	@Override
	public String getName() {
		return "UmengAuthorize";
	}

	/**
	 * 获取token
	 * 
	 * @author: ylc
	 */
	public String getAuthorize() {
		String tokenUrl = TOKEN_URL;
		String email = USERNAME;
		String password = PASSWORD;
		parameterMap.put("email", email);
		parameterMap.put("password", password);
		String response = super.doPost(tokenUrl, parameterMap);
		if (!StringUtil.isNullOrEmpty(response)) {
			//把json解析为javaBean
			authorize = JSON.parseObject(response, Authorize.class);
		}
		return authorize == null ? new Authorize().getAuth_token() : authorize.getAuth_token();
	}

}
