package com.unifig.organ.controller;


import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.DeptVo;
import com.unifig.organ.service.DeptRecordService;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 部门管理操作记录表 前端控制器
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Controller
@RequestMapping("/org/deptRecord")
@ApiIgnore
public class UmsDeptRecordController {

	@Autowired
	private DeptRecordService deptRecordService;

	/**
	 * 分页获取机构信息操作信息列表
	 *
	 * @return
	 */
	@ApiOperation(value = "分页获取机构信息操作信息列表", notes = "根据deptVo对象进行条件查询")
	@GetMapping(value = "/findeDeptRecordListByDeptVo")
	@ResponseBody
	public ResultData findeDeptRecordListByDeptVo(@CurrentUser UserCache userCache, DeptVo deptVo) {
		return deptRecordService.findeDeptRecordListByDeptVo(userCache, deptVo);
	}
}

