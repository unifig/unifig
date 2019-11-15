/**
 * FileName: 订单配送
 * Author:
 * Date:     2019-07-24
 * Description: 订单配送
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.service.OmsOrderDeliverymanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import com.unifig.result.ResultData;

import java.util.Arrays;
import java.util.List;
import com.unifig.mall.bean.model.OmsOrderDeliveryman;

/**
 * <p>
 * 订单配送 控制器
 * </p>
 *
 *
 * @since 2019-07-24
 */
@RestController
@RequestMapping("/omsOrderDeliveryman")
@Api(value = "/omsOrderDeliveryman", tags = "订单配送")
public class OmsOrderDeliverymanController {

	private Logger logger=LoggerFactory.getLogger(getClass());

	@Autowired
	private OmsOrderDeliverymanService omsOrderDeliverymanService;

	/**
 	* 查询分页数据
	 */
	@ApiOperation(value = "查询分页数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageNum", value = "当前页", required = true, dataType = "int", paramType = "query"),
		@ApiImplicitParam(name = "pageSize", value = "单页条数", required = true, dataType = "int", paramType = "query")
	})
	@GetMapping(value = "/list")
	public ResultData<OmsOrderDeliveryman> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,@RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
		return omsOrderDeliverymanService.findListByPage(pageNum,pageSize);
	}

	/**
	 * 查询分页数据
	 */
	@ApiOperation(value = "客户端配送员获取配送列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "单页条数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "状态  0 配送中   1  已完成", required = true, dataType = "int", paramType = "query")
	})
	@GetMapping(value = "/client/list")
	public ResultData<OmsOrderDeliveryman> clientList(@RequestParam(name = "pageNum", defaultValue = "1",required = false) int pageNum,
													  @RequestParam(name = "pageSize", defaultValue = "5",required = false) int pageSize,
													  @RequestParam(name = "status", defaultValue = "0",required = false) int status,
													  @CurrentUser UserCache userCache
	){
		return omsOrderDeliverymanService.clientList(pageNum,pageSize,status,userCache.getUserId());
	}



	/**
	 * 分配订单
	 */
	@ApiOperation(value = "分配订单")
	@PostMapping(value = "/admin/add")
	public ResultData add(@ApiParam(name = "ids", value = "订单id 集合", required = true) @RequestParam(name = "ids")String[] ids,
						  @ApiParam(name = "userId", value = "配送员id", required = true) @RequestParam(name = "userId")String userId,
						  @ApiParam(name = "userName", value = "配送员名称", required = true) @RequestParam(name = "userName")String userName){
		return omsOrderDeliverymanService.add(Arrays.asList(ids),userId,userName);
	}


	/**
 	* 配送员更改状态
 	*/
	@ApiOperation(value = "配送员更改状态")
	@GetMapping(value = "/update")
	public ResultData update(@RequestParam(name = "uuid") String uuid){
		return omsOrderDeliverymanService.update(uuid);
	}

}
