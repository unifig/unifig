package com.unifig.organ.service;

import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysRoleEntity;
import com.unifig.page.Page;
import com.unifig.result.ResultData;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *

 * @date 2018-10-24
 */
public interface UmsSysRoleService {

	SysRoleEntity queryObject(Long roleId);

	List<SysRoleEntity> queryList(Map<String, Object> map);

	int queryTotal(Map<String, Object> map);

	void save(SysRoleEntity role);

	void update(SysRoleEntity role);

	void deleteBatch(Long[] roleIds);

	/**
	 * 查询用户创建的角色ID列表
	 */
	List<Long> queryRoleIdList(Long createUserId);

	/**
	 * 分页查询角色审批选择范围
	 *
	 * @return
	 */
	Page<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto, int pageNmu);

	/**
	 * 获取除了管理员角色外的角色列表
	 *
	 * @return
	 */
	ResultData findRolelist();
}
