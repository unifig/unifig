package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.unifig.organ.domain.Role;
import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.mapper.RoleMapper;
import com.unifig.organ.service.UmsSysRoleDeptService;
import com.unifig.organ.service.UmsSysRoleService;
import com.unifig.organ.service.UmsSysUserService;
import com.unifig.organ.dao.UmsSysRoleDao;
import com.unifig.organ.dto.PageHelper;
import com.unifig.organ.model.SysRoleEntity;
import com.unifig.organ.service.UmsSysRoleMenuService;
import com.unifig.page.Page;
import com.unifig.result.ResultData;
import com.unifig.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 角色
 *

 * @date 2018-10-24
 */
@Service("sysRoleService")
public class UmsUmsSysRoleServiceImpl implements UmsSysRoleService {
	@Autowired
	private UmsSysRoleDao umsSysRoleDao;
	@Autowired
	private UmsSysRoleMenuService sysRoleMenuService;
	@Autowired
	private UmsSysUserService sysUserService;
	@Autowired
	private UmsSysRoleDeptService sysRoleDeptService;
	@Autowired
	private RoleMapper roleMapper;

	@Override
	public SysRoleEntity queryObject(Long roleId) {
		return umsSysRoleDao.queryObject(roleId);
	}

	@Override
	public List<SysRoleEntity> queryList(Map<String, Object> map) {
		return umsSysRoleDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return umsSysRoleDao.queryTotal(map);
	}

	@Override
	@Transactional
	public void save(SysRoleEntity role) {
		role.setCreateTime(new Date());
		umsSysRoleDao.save(role);

		//检查权限是否越权
		checkPrems(role);

		//保存角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
	}

	@Override
	@Transactional
	public void update(SysRoleEntity role) {
		umsSysRoleDao.update(role);

		//检查权限是否越权
		checkPrems(role);

		//更新角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
		//保存角色与部门关系
		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
	}

	@Override
	@Transactional
	public void deleteBatch(Long[] roleIds) {
		roleMapper.delete(Condition.create().in("role_id", roleIds));
	}

	@Override
	public List<Long> queryRoleIdList(Long createUserId) {
		return umsSysRoleDao.queryRoleIdList(createUserId);
	}

	/**
	 * 检查权限是否越权
	 */
	private void checkPrems(SysRoleEntity role) {
		//如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
		if (role.getCreateUserId() == Constant.SUPER_ADMIN) {
			return;
		}

		//查询用户所拥有的菜单列表
		List<Long> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());

	}

	@Override
	public Page<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto, int pageNum) {
		PageHelper.startPage(pageNum, Constant.pageSize);
		umsSysRoleDao.queryPageByDto(userWindowDto);
		return PageHelper.endPage();
	}

	/**
	 * 获取除了管理员角色外的角色列表
	 *
	 * @return
	 */
	@Override
	public ResultData findRolelist() {
		ArrayList<Long> longs = new ArrayList<>();
		longs.add(1L);
		longs.add(2L);
		List<Role> roles = roleMapper.selectList(Condition.create().notIn("role_id", longs));
		return ResultData.result(true).setData(roles);
	}
}
