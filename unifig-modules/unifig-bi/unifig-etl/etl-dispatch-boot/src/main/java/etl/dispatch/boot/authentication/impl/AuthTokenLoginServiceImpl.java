package etl.dispatch.boot.authentication.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tools.plugin.redis.RedisHolder;
import com.tools.plugin.utils.NewMapUtil;
import com.tools.plugin.utils.cipher.MD5;
import com.tools.plugin.utils.system.InternetProtocol;

import etl.dispatch.base.enums.LoginAuthResultEnums;
import etl.dispatch.base.enums.LogoutResultEnums;
import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.boot.authentication.IAuthTokenLoginService;
import etl.dispatch.boot.entity.ConfUserInfo;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfUserInfo;
import etl.dispatch.util.BeanUtil;
import etl.dispatch.util.OsUtils;
import etl.dispatch.util.StringUtil;
import redis.clients.jedis.BinaryJedisCluster;


/**
 * <系统的登录验证实现类>
 */
@Service
public class AuthTokenLoginServiceImpl implements IAuthTokenLoginService {
	private static Logger logger = LoggerFactory.getLogger(AuthTokenLoginServiceImpl.class);
	private static final String tokenServerKey = "etl.dispatch";
	private static final int DEFAULT_INVALID_TIME = 60 * 30;//30分钟失效
	private static BinaryJedisCluster jedisCluster;
	static {
		boolean develop = PropertiesHolder.getBooleanProperty("webapp.service.develop");
		String redisCluster = null;
		String requirePass  = null;
		//非开发环境 且 操作系统非 Windows，使用正式库配置
		if(!develop && OsUtils.isShellModel()){
			redisCluster=PropertiesHolder.getProperty("plugin.redis.pro.address");
			requirePass =PropertiesHolder.getProperty("plugin.redis.pro.password");
		}else{
			redisCluster=PropertiesHolder.getProperty("plugin.redis.dev.address");
			requirePass =PropertiesHolder.getProperty("plugin.redis.dev.password");
		}
		jedisCluster = RedisHolder.getJedisCluster(redisCluster, requirePass);
	}
	
	@Autowired
	private IConfUserInfo iConfUserInfo;
	/**
	 * 执行登录校验
	 */
	public VisitsResult authc(String loginName, String loginPass, HttpServletRequest request) {
		// 查询用户表数据
		List<ConfUserInfo> list = iConfUserInfo.selectByMap(new NewMapUtil().set("user_name", loginName).get());
		if (list.size() > 1) {
			logger.error(loginName + ":用户名重复！！！！");
			return  new VisitsResult(
					new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_NOT_UNIQUE.toString()).get());
		}
		if (list == null || list.size() == 0 || list.isEmpty()) {
			return  new VisitsResult(
					new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_USER_NOTEXSIST.toString()).get());
		}
		ConfUserInfo userInfo = list.get(0);
		// 获取用户密码
		String passWord = userInfo.getPassWord();
		if (!StringUtil.isNullOrEmpty(passWord) && !StringUtil.isNullOrEmpty(loginPass)) {
			// 数据库存储MD5密码
			if ((new Md5Hash(loginPass).toHex()).equals(passWord)) {
				String remoteAddr = InternetProtocol.getRemoteAddr(request);
				String adoptToken = this.sessionLoginCache(userInfo, remoteAddr);
				return new VisitsResult(adoptToken,userInfo);
			} else {
				return new VisitsResult(new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_PWD_INCORRECT.toString()).get());
			}
		} else {
			return new VisitsResult(new NewMapUtil("message", LoginAuthResultEnums.LOGIN_FAIL_PWD_INCORRECT.toString()).get());
		}
	}

	
	/**
	 * <用户注销方法>
	 **/
	public VisitsResult logout(String adoptToken) {
		if (!isAuthenticated(adoptToken)) {
			return new VisitsResult(new NewMapUtil("message", LogoutResultEnums.LOGOUT_SUCCESS.toString()).get());
		} else {
			if (logouts(adoptToken)) {
				return new VisitsResult(new NewMapUtil("message", LogoutResultEnums.LOGOUT_SUCCESS.toString()).get());
			} else {
				return new VisitsResult(new NewMapUtil("message", LogoutResultEnums.LOGOUT_FAIL_EXCEPTION.toString()).get());
			}
		}
	}
	
	
	/**
	 * 注销登录令牌<br/>
	 */
	public boolean logouts(String adoptToken) {
		if (null != jedisCluster) {
			try {
				jedisCluster.del(adoptToken.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to delete redis value , error message is" + e.getMessage() + " ;Please contact the administrator");
				return false;
			}
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
			return false;
		}
		return true;
	}
	@Override
	public boolean isAuthenticated(String adoptToken) {
		ConfUserInfo userInfo = this.getCurrentUser(adoptToken);
		if (null == userInfo) {
			return false;
		}
		try {
			jedisCluster.setex(adoptToken.getBytes("utf-8"),DEFAULT_INVALID_TIME, BeanUtil.objectToByte(userInfo));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public ConfUserInfo getCurrentUser(String adoptToken) {
		byte[] userBytesInfo = null;
		if (null != jedisCluster) {
			try {
				userBytesInfo = jedisCluster.get(adoptToken.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed to get redis redis , error message is" + e.getMessage() + " ;Please contact the administrator");
			}
		} else {
			logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
		}
		if (null == userBytesInfo || userBytesInfo.length == 0) {
			return null;
		}
		return (ConfUserInfo) BeanUtil.byteToObject(userBytesInfo);
	}

	/**
	 * <登录用户session缓存>
	 */
	private String sessionLoginCache(ConfUserInfo userInfo, String remoteAddr) {
				
		// 生成登录用户令牌
		String adoptToken = this.generateToken(userInfo.getUserName(), userInfo.getPassWord(), remoteAddr);
		// 入库redis公有缓存
		try {
			if (null != jedisCluster) {
				// 重复的令牌，再次生成
				if (null != jedisCluster.get(adoptToken.getBytes("utf-8"))) {
					adoptToken = this.generateToken(userInfo.getUserName(), userInfo.getPassWord(), remoteAddr);
				}
				// session到Redis库
				jedisCluster.setex(adoptToken.getBytes("utf-8"), DEFAULT_INVALID_TIME, BeanUtil.objectToByte(userInfo));
			} else {
				logger.error("Failed to get the distributed redis connection , connection is null; Please contact the administrator");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to get the distributed redis connection , error message is" + e.getMessage() + " ;Please contact the administrator");
		}

		return adoptToken;
	}

	/**
	 * token生成规则: 远端地址+ 2位userName校验位 + 2位passWord校验位
	 * 
	 * @param userName
	 * @param passWord
	 * @return
	 */
	private String generateToken(String userName, String passWord , String remoteAddr) {
		String userNameMd5 = MD5.encryptToHex(userName);
		String passWordMd5 = MD5.encryptToHex(passWord);
		String userNameMd5Verified = userNameMd5.substring(userNameMd5.length() - 2);
		String passWordMd5Verified = passWordMd5.substring(passWordMd5.length() - 2);
		StringBuilder dataToken = new StringBuilder();
		dataToken.append(MD5.encryptToHex(remoteAddr)).append(userNameMd5Verified).append(passWordMd5Verified).append(MD5.encryptToHex(tokenServerKey));
		return dataToken.toString();
	}
}
