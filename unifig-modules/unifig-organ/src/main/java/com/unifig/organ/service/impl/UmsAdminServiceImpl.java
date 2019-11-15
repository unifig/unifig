package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.context.Constants;
import com.unifig.context.RedisConstants;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.dao.UmsSysUserDao;
import com.unifig.organ.dto.UmsAdminParam;
import com.unifig.organ.mapper.UmsAdminLoginLogMapper;
import com.unifig.organ.model.SysUserEntity;
import com.unifig.organ.model.UmsAdminLoginLog;
import com.unifig.organ.service.UmsAdminService;
import com.unifig.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * UmsAdminService实现类
 *    on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsSysUserDao, SysUserEntity> implements UmsAdminService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${jwt.tokenHead}")
	private String tokenHead;
	@Autowired
	private UmsAdminLoginLogMapper loginLogMapper;
	@Autowired
	private UmsSysUserDao umsSysUserDao;
	@Autowired
	private CacheRedisUtils cacheRedisUtils;


	@Override
	public SysUserEntity register(UmsAdminParam umsAdminParam) {
		SysUserEntity userEntity = new SysUserEntity();
		BeanUtils.copyProperties(umsAdminParam, userEntity);
		userEntity.setCreateTime(new Date());
		userEntity.setStatus(1);

		//查询是否有相同用户名的用户

		SysUserEntity userEntitydb = selectByMobile(userEntity.getMobile());
		if (userEntitydb != null) {
			return null;
		}
		//将密码进行加密操作
		String md5Password = MD5Util.getMD5(userEntity.getPassword());
		userEntity.setPassword(md5Password);
		umsSysUserDao.insert(userEntity);
		return userEntity;
	}

	@Override
	public String login(String mobile, String password, String passwordType) {
		String token = null;
		//密码需要客户端加密后传递
		SysUserEntity userEntity = selectByMobile(mobile);
		if (null == userEntity) {
			throw new RuntimeException("账号不存在,请核实!");
		}
		Integer status = userEntity.getStatus();
		if (0 == status) {
			throw new RuntimeException("账号已禁用,请联系管理员!");
		}
		if (StringUtils.isBlank(passwordType)){
			password = MD5Util.getMD5(password);
		}
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEntity.getUserId(), password);
		try {
			UserCache userAdmimCache = new UserCache();
			BeanUtils.copyProperties(userEntity, userAdmimCache);
			userAdmimCache.setUserId(String.valueOf(userEntity.getUserId()));
			userAdmimCache.setDeptId(String.valueOf(userEntity.getDeptId()));
			userAdmimCache.setStatus(String.valueOf(userEntity.getStatus()));
			Map<String, Object> userInfoMap = BeanMapUtils.convertBean2Map(userAdmimCache, new String[]{"serialVersionUID"});
			cacheRedisUtils.hmset(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_ADMIN_USER_KAY).append(userEntity.getUserId()).toString(), userInfoMap);
			token = jwtTokenUtil.generateToken(userEntity.getUserId(), userEntity.getUsername(), Constants.RATEL_ADMIN_TAG, userEntity.getMobile(), null, null, String.valueOf(userEntity.getDeptId()),null);
			//将用户信息存入缓存
			cacheRedisUtils.set(new StringBuilder().append(RedisConstants.RATEL_PATH_DEF).append(RedisConstants.RATEL_JWT_ADMIN_TOKEN_USER_KAY).append(userEntity.getUserId()).toString(), token);
			updateLoginTimeByMobile(mobile);
			insertLoginLog(mobile);
		} catch (AuthenticationException e) {
			LOGGER.warn("登录异常:{}", e.getMessage());
		}
		return token;
	}

	/**
	 * 添加登录记录
	 *
	 * @param mobile 手机号
	 */
	private void insertLoginLog(String mobile) {
		SysUserEntity userEntity = selectByMobile(mobile);
		UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
		loginLog.setAdminId(userEntity.getUserId());
		loginLog.setCreateTime(new Date());
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		loginLog.setIp(request.getRemoteAddr());
		loginLogMapper.insert(loginLog);
	}


	@Override
	public String refreshToken(String oldToken) {
		String token = oldToken.substring(tokenHead.length());
		if (jwtTokenUtil.canRefresh(token)) {
			return jwtTokenUtil.refreshToken(token);
		}
		return null;
	}

	/**
	 * 根据用户名修改登录时间
	 */
	private void updateLoginTimeByMobile(String mobile) {
		SysUserEntity userEntity = selectByMobile(mobile);
		if (userEntity != null) {
			userEntity.setLoginTime(new Date());
		}
		umsSysUserDao.updateById(userEntity);
	}

	/**
	 * 检测用户手机号 是否可用
	 */
	@Override
	public SysUserEntity checkMobile(String mobile) {
		EntityWrapper<SysUserEntity> example = new EntityWrapper<SysUserEntity>();
		example.eq("mobile", mobile);
		List<SysUserEntity> sysUserEntities = selectList(example);
		if (sysUserEntities.size() > 0) {
			return null;
		}
		return sysUserEntities.get(0);
	}

	/**
	 * 根据登录账号查询用户信息
	 */
	@Override
	public SysUserEntity selectByMobile(String mobile) {
		EntityWrapper<SysUserEntity> example = new EntityWrapper<SysUserEntity>();
		example.eq("username", mobile);
		List<SysUserEntity> sysUserEntities = selectList(example);
		if (sysUserEntities.size() == 1) {
			return sysUserEntities.get(0);
		}
		return null;
	}


}
