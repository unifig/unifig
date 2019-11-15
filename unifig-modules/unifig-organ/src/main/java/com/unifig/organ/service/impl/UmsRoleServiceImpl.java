package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.unifig.organ.dao.UmsRolePermissionRelationDao;
import com.unifig.organ.mapper.RoleMapper;
import com.unifig.organ.mapper.UmsRoleMapper;
import com.unifig.organ.mapper.UmsRolePermissionRelationMapper;
import com.unifig.organ.model.*;
import com.unifig.organ.service.UmsRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台角色管理Service实现类
 *    on 2018/9/30.
 */
@Service
public class UmsRoleServiceImpl implements UmsRoleService {
	@Autowired
	private UmsRoleMapper roleMapper;
	@Autowired
	private UmsRolePermissionRelationMapper rolePermissionRelationMapper;
	@Autowired
	private UmsRolePermissionRelationDao rolePermissionRelationDao;

	@Autowired
	private RoleMapper roleMapperPlus;


	@Override
	public int create(UmsRole role) {
		role.setCreateTime(new Date());
		role.setStatus(1);
		role.setAdminCount(0);
		role.setSort(0);
		return roleMapper.insert(role);
	}

	@Override
	public int update(Long id, UmsRole role) {
		role.setId(id);
		return roleMapper.updateByPrimaryKey(role);
	}

	@Override
	public int delete(String ids) {
		//UmsRoleExample example = new UmsRoleExample();
		// example.createCriteria().andIdIn(ids);
		//return roleMapper.deleteByExample(example);
		return roleMapperPlus.delete(Condition.create().in("role_id", ids));
	}

	@Override
	public List<UmsPermission> getPermissionList(Long roleId) {
		return rolePermissionRelationDao.getPermissionList(roleId);
	}

	@Override
	public int updatePermission(Long roleId, List<Long> permissionIds) {
		//先删除原有关系
		UmsRolePermissionRelationExample example = new UmsRolePermissionRelationExample();
		example.createCriteria().andRoleIdEqualTo(roleId);
		rolePermissionRelationMapper.deleteByExample(example);
		//批量插入新关系
		List<UmsRolePermissionRelation> relationList = new ArrayList<>();
		for (Long permissionId : permissionIds) {
			UmsRolePermissionRelation relation = new UmsRolePermissionRelation();
			relation.setRoleId(roleId);
			relation.setPermissionId(permissionId);
			relationList.add(relation);
		}
		return rolePermissionRelationDao.insertList(relationList);
	}

	@Override
	public List<UmsRole> list() {
		return roleMapper.selectByExample(new UmsRoleExample());
	}
}
