/**
 * FileName: 用户关系表
 * Author:   maxl
 * Date:     2019-08-30
 * Description: 用户关系表
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.organ.controller;


import com.unifig.organ.service.UmsIntegrationChangeHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import com.unifig.result.ResultData;
import java.util.List;
import com.unifig.organ.service.UmsMemberScService;
import com.unifig.organ.model.UmsMemberSc;

/**
 * <p>
 * 用户关系表 控制器
 * </p>
 *
 *
 * @since 2019-08-30
 */
@RestController
@RequestMapping("/umsMemberSc")
@Api(value = "/umsMemberSc", tags = "用户关系")
public class UmsMemberScController {
	private Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private UmsMemberScService umsMemberScService;


	/**
 	* 查询分页数据
	 */
	@ApiOperation(value = "查询分页数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNum", value = "当前页", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "pageSize", value = "单页条数", required = true, dataType = "int", paramType = "query")
	})
	@GetMapping(value = "/list")
	public ResultData<UmsMemberSc> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,@RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
		return umsMemberScService.findListByPage(pageNum,pageSize);
	}


	/**
	 * 根据id查询
 	*/
	@ApiOperation(value = "根据id查询数据")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "String", paramType = "query")
	@GetMapping(value = "/getById")
	public ResultData<UmsMemberSc> getById(@RequestParam("id") String id){
		return umsMemberScService.getById(id);
	}

	/**
 	* 新增
 	*/
	@ApiOperation(value = "新增数据")
	@PostMapping(value = "/add")
	public ResultData add(@RequestBody UmsMemberSc umsMemberSc){
		return umsMemberScService.add(umsMemberSc);
	}

	/**
 	* 删除
 	*/
	@ApiOperation(value = "删除数据")
	@GetMapping(value = "/del")
	public ResultData delete(@RequestParam("ids") List<String> ids){
		return umsMemberScService.delete(ids);
	}

	/**
 	* 修改
 	*/
	@ApiOperation(value = "更新数据")
	@PostMapping(value = "/update")
	public ResultData update(@RequestBody UmsMemberSc umsMemberSc){
		return umsMemberScService.update(umsMemberSc);
	}

}
