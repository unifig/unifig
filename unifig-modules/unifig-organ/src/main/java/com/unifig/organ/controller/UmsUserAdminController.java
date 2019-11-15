package com.unifig.organ.controller;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.dto.UmsAdminLoginParam;
import com.unifig.organ.dto.UmsAdminParam;
import com.unifig.organ.domain.CommonResult;
import com.unifig.organ.model.SysUserEntity;
import com.unifig.organ.service.UmsAdminService;
import com.unifig.utils.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台用户管理
 *    on 2018/4/26.
 */
@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
@ApiIgnore
public class UmsUserAdminController {
	@Autowired
	private UmsAdminService adminService;

	@Autowired
	private UserTokenUtil userTokenUtil;

	@Value("${jwt.tokenHeader}")
	private String tokenHeader;
	@Value("${jwt.tokenHead}")
	private String tokenHead;

	@ApiOperation(value = "用户注册")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Object register(@RequestBody UmsAdminParam umsAdminParam, BindingResult result) {
		SysUserEntity userEntity = adminService.register(umsAdminParam);
		if (userEntity == null) {
			new CommonResult().failed();
		}
		return new CommonResult().success(userEntity);
	}

	@ApiOperation(value = "登录以后返回token")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(@RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
		String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword(), umsAdminLoginParam.getPasswordType());
		if (token == null) {
			return new CommonResult().failed("用户名或密码错误");
		}
		UserCache userCacheFromToken = userTokenUtil.getUserCacheFromTokenNoHeade(token);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("token", token);
		tokenMap.put("tokenHead", tokenHead);
		tokenMap.put("userName", userCacheFromToken.getUsername());
		tokenMap.put("deptId", userCacheFromToken.getDeptId());
		return new CommonResult().success(tokenMap);
	}

	@ApiOperation(value = "刷新token")
	@RequestMapping(value = "/token/refresh", method = RequestMethod.GET)
	@ResponseBody
	public Object refreshToken(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		String refreshToken = adminService.refreshToken(token);
		if (refreshToken == null) {
			return new CommonResult().failed();
		}
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("token", token);
		tokenMap.put("tokenHead", tokenHead);
		return new CommonResult().success(tokenMap);
	}

	@ApiOperation(value = "获取当前登录用户信息")
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ResponseBody
	public Object getAdminInfo(Principal principal) {
//        String  username = principal.getName();
		String mobile = "admin";
		SysUserEntity userEntity = adminService.selectByMobile(mobile);
		Map<String, Object> data = new HashMap<>();
		data.put("username", userEntity.getUsername());
		data.put("roles", new String[]{"TEST"});
		data.put("icon", userEntity.getIcon());
		return new CommonResult().success(data);
	}

	@ApiOperation(value = "登出功能")
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public Object logout() {
		return new CommonResult().success(null);
	}
}
