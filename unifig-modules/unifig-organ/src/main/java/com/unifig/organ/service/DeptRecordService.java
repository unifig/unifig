package com.unifig.organ.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.DeptRecord;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.domain.DeptVo;
import com.unifig.result.ResultData;

/**
 * <p>
 * 部门管理操作记录表 服务类
 * </p>
 *
 *
 * @since 2019-03-06
 */
public interface DeptRecordService extends IService<DeptRecord> {

	/**
	 * 分页获取机构信息操作信息列表
	 *
	 * @param userCache
	 * @param deptVo
	 * @return
	 */
	ResultData findeDeptRecordListByDeptVo(UserCache userCache, DeptVo deptVo);
}
