package com.unifig.organ.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.Dept;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.domain.DeptVo;
import com.unifig.result.ResultData;

import java.util.List;

/**
 * <p>
 * 部门管理 服务类
 * </p>
 *
 *
 * @since 2019-03-06
 */
public interface DeptService extends IService<Dept> {
	/**
	 * Feign使用的根据部门ID获取部门信息
	 *
	 * @param id
	 * @return
	 */
	Dept findDeptById(Long id);

	/**
	 * 根据机构id获取机构信息
	 *
	 *
	 * @param userCache
	 * @param id
	 * @return
	 */
	ResultData selectDeptById(UserCache userCache, Long id);

	/**
	 * 新增机构信息
	 *
	 * @param userCache
	 * @param dept
	 * @return
	 */
	ResultData createDept(UserCache userCache, Dept dept);

	/**
	 * 停用启用机构
	 *
	 * @param userCache
	 * @param id
	 * @param status
	 * @return
	 */
	ResultData upadteDeptStatus(UserCache userCache, String ids, String status);

	/**
	 * 重置管理员密码
	 *
	 * @param userCache
	 * @param id
	 * @param password
	 * @return
	 */
	ResultData upadteDeptAdminPassword(UserCache userCache, Long id, String password);

	/**
	 * 分页获取机构信息列表
	 *
	 * @param userCache
	 * @param deptVo
	 * @return
	 */
	ResultData findeDeptListByDeptVo(UserCache userCache, DeptVo deptVo);

	/**
	 * 获取当前用户机构及下级机构列表(不传deptId默认查当前用户部门,传了查deptId部门)
	 *
	 * @param userCache
	 * @param deptId
	 * @param resultType
	 * @param user
	 * @return
	 */
	ResultData findeDeptListByDeptId(UserCache userCache, String deptId, String resultType, String user);

	/**
	 * 根据部门id获取子级全部id
	 *
	 * @param deptId
	 * @return
	 */
	List<Long> findAllSubDept(Long deptId);
}
