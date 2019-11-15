package com.unifig.organ.controller;


import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.User;
import com.unifig.organ.domain.UserVo;
import com.unifig.organ.service.UserService;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 系统用户 前端控制器
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Controller
@RequestMapping("/org/user")
@ApiIgnore
public class UmsUserController {

	@Autowired
	private UserService userService;

	/**
	 * 新增账号信息
	 *
	 * @return
	 */
	@ApiOperation(value = "新增账号信息", notes = "根据user对象新增账号信息")
	@PostMapping(value = "/createUser")
	@ResponseBody
	public ResultData createUser(@CurrentUser UserCache userCache, @RequestBody User user) {
		return userService.createUser(userCache, user);
	}

	/**
	 * 更新账号信息
	 *
	 * @return
	 */
	@ApiOperation(value = "更新账号信息", notes = "根据user对象更新账号信息")
	@PostMapping(value = "/updateUser")
	@ResponseBody
	public ResultData updateUser(@CurrentUser UserCache userCache, @RequestBody User user) {
		return userService.updateUser(userCache, user);
	}


	/**
	 * 删除账号信息
	 *
	 * @return
	 */
	@ApiOperation(value = "删除账号信息")
	@GetMapping(value = "/deleteUser")
	@ResponseBody
	public ResultData deleteUser(@CurrentUser UserCache userCache, Long id) {
		return userService.deleteUser(userCache, id);
	}

	/**
	 * 分页获取账号信息列表
	 *
	 * @return
	 */
	@ApiOperation(value = "分页获取账号信息列表", notes = "根据userVo对象进行条件查询")
	@GetMapping(value = "/findeUserdListByUsertVo")
	@ResponseBody
	public ResultData findeUserdListByUsertVo(@CurrentUser UserCache userCache, UserVo userVo) {
		return userService.findeUserdListByUsertVo(userCache, userVo);
	}
}

