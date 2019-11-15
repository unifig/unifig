package com.unifig.organ.controller;

import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.service.UmsSysRoleDeptService;
import com.unifig.organ.service.UmsSysRoleService;
import com.unifig.organ.dto.R;
import com.unifig.organ.model.SysRoleEntity;
import com.unifig.organ.service.UmsSysRoleMenuService;
import com.unifig.result.ResultData;
import com.unifig.utils.Constant;
import com.unifig.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *

 * @date 2018-10-24
 */
@RestController
@RequestMapping("/sys/role")
@ApiIgnore
public class UmsSysRoleController extends AbstractController {
	@Autowired
	private UmsSysRoleService sysRoleService;
	@Autowired
	private UmsSysRoleMenuService sysRoleMenuService;
	@Autowired
	private UmsSysRoleDeptService sysRoleDeptService;

	/**
	 * 角色列表
	 */
	@RequestMapping("/findRolelist")
	public ResultData findRolelist() {
		return sysRoleService.findRolelist();
	}

	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	public ResultData list(@RequestParam Map<String, Object> params) {
		//如果不是超级管理员，则只查询自己创建的角色列表
		if (getUserId() != Constant.SUPER_ADMIN) {
			params.put("createUserId", getUserId());
		}

		//查询列表数据
		Query query = new Query(params);
		List<SysRoleEntity> list = sysRoleService.queryList(query);
		int total = sysRoleService.queryTotal(query);

		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return ResultData.result(true).setMsg("true").setData(pageUtil.getList()).setCount(total);
		// return R.ok().put("page", pageUtil);
	}

	/**
	 * 角色列表
	 */
	@RequestMapping("/select")
	public R select() {
		Map<String, Object> map = new HashMap<>();

		//如果不是超级管理员，则只查询自己所拥有的角色列表
		if (getUserId() != Constant.SUPER_ADMIN) {
			map.put("createUserId", getUserId());
		}
		List<SysRoleEntity> list = sysRoleService.queryList(map);

		return R.ok().put("list", list);
	}

	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	public R info(@PathVariable("roleId") Long roleId) {
		SysRoleEntity role = sysRoleService.queryObject(roleId);

		//查询角色对应的菜单
		List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);

		//查询角色对应的部门
		List<Long> deptIdList = sysRoleDeptService.queryDeptIdList(roleId);
		role.setDeptIdList(deptIdList);

		return R.ok().put("role", role);
	}

	/**
	 * 保存角色
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SysRoleEntity role) {
		role.setCreateUserId(getUserId());
		sysRoleService.save(role);

		return R.ok();
	}

	/**
	 * 修改角色
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysRoleEntity role) {

		role.setCreateUserId(getUserId());
		sysRoleService.update(role);

		return R.ok();
	}

	/**
	 * 删除角色
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] roleIds) {
		sysRoleService.deleteBatch(roleIds);

		return R.ok();
	}
}
