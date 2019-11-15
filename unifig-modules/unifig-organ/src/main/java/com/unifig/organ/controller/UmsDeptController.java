package com.unifig.organ.controller;


import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.Dept;
import com.unifig.organ.domain.DeptVo;
import com.unifig.organ.service.DeptService;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * 部门管理 前端控制器
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Controller
@RequestMapping("/org/dept")
@ApiIgnore
public class UmsDeptController {

	@Autowired
	private DeptService deptService;

	/**
	 * Feign使用的根据部门ID获取部门信息
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/feign/findDeptById/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Dept findDeptById(@PathVariable Long id) {
		return deptService.findDeptById(id);
	}

	/**
	 * Feign使用的根据部门ID子集部门id集合
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/feign/findAllSubDept/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> findAllSubDept(@PathVariable Long id) {
		return deptService.findAllSubDept(id);
	}


	/**
	 * 根据机构id获取机构信息
	 *
	 * @return
	 */
	@ApiOperation(value = "根据机构id获取机构信息")
	@GetMapping(value = "/selectDeptById")
	@ResponseBody
	public ResultData selectDeptById(@CurrentUser UserCache userCache, Long id) {
		return deptService.selectDeptById(userCache, id);
	}

	/**
	 * 新增机构信息
	 *
	 * @return
	 */
	@ApiOperation(value = "新增机构信息", notes = "根据dept对象新增机构信息")
	@PostMapping(value = "/createDept")
	@ResponseBody
	public ResultData createDept(@CurrentUser UserCache userCache, @RequestBody Dept dept) {
		return deptService.createDept(userCache, dept);
	}

	/**
	 * 停用启用机构(支持批量)
	 *
	 * @return
	 */
	@ApiOperation(value = "停用启用机构(支持批量)")
	@GetMapping(value = "/upadteDeptStatus")
	@ResponseBody
	public ResultData upadteDeptStatus(@CurrentUser UserCache userCache, String ids, String status) {

		return deptService.upadteDeptStatus(userCache, ids, status);
	}

	/**
	 * 重置管理员密码
	 *
	 * @return
	 */
	@ApiOperation(value = "重置管理员密码")
	@GetMapping(value = "/upadteDeptAdminPassword")
	@ResponseBody
	public ResultData upadteDeptAdminPassword(@CurrentUser UserCache userCache, Long id, String password) {

		return deptService.upadteDeptAdminPassword(userCache, id, password);
	}

	/**
	 * 分页获取下级机构信息列表
	 *
	 * @return
	 */
	@ApiOperation(value = "分页获取下级机构信息列表", notes = "根据deptVo对象进行条件查询")
	@PostMapping(value = "/findeDeptListByDeptVo")
	@ResponseBody
	public ResultData findeDeptListByDeptVo(@CurrentUser UserCache userCache, @RequestBody DeptVo deptVo) {
		return deptService.findeDeptListByDeptVo(userCache, deptVo);
	}

	/**
	 * 获取当前用户机构及下级机构列表(不传deptId默认查当前用户部门,传了查deptId部门)
	 *
	 * @return
	 */
	@ApiOperation(value = "获取当前用户机构及下级机构列表(不传deptId默认查当前用户部门,传了查deptId部门)")
	@GetMapping(value = "/findeDeptListByDeptId")
	@ResponseBody
	public ResultData findeDeptListByDeptId(@CurrentUser UserCache userCache, String deptId, String resultType,String user) {
		return deptService.findeDeptListByDeptId(userCache, deptId, resultType,user);
	}
}

