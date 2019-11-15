package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.DeptRecord;
import com.unifig.organ.dao.DeptRecordMapper;
import com.unifig.organ.domain.DeptVo;
import com.unifig.organ.service.DeptRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部门管理操作记录表 服务实现类
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Service
public class DeptRecordServiceImpl extends ServiceImpl<DeptRecordMapper, DeptRecord> implements DeptRecordService {

	@Autowired
	private DeptRecordMapper deptRecordMapper;

	/**
	 * 分页获取机构信息操作信息列表
	 *
	 * @param userCache
	 * @param deptVo
	 * @return
	 */
	@Override
	public ResultData findeDeptRecordListByDeptVo(UserCache userCache, DeptVo deptVo) {
		//创建条件构造器
		EntityWrapper<DeptRecord> ew = new EntityWrapper<DeptRecord>();
		ew.setEntity(new DeptRecord());
		//当前机构id
		Long deptId = deptVo.getDeptId();
		if (deptId == null) {
			return ResultData.result(false).setMsg("机构id不能为空");
		}
		ew.eq("dept_id", deptId);
		//当前页
		Integer pageNum = deptVo.getPageNum();
		if (null == pageNum) {
			pageNum = 1;
		}
		Integer count = deptRecordMapper.selectCount(ew);
		//页大小
		Integer pageSize = deptVo.getPageSize();
		if (null == pageSize) {
			pageSize = count;
		}
		ew.orderBy(" create_time desc ");
		List<DeptRecord> deptRecordList = deptRecordMapper.selectPage(new Page<DeptRecord>(pageNum, pageSize), ew);
		return ResultData.result(true).setData(deptRecordList).setCount(count);
	}
}
