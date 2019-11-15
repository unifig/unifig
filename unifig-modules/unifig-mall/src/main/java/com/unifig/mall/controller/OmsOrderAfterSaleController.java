/**
 * FileName: OmsOrderAfterSaleController
 * Author:
 * Date:     2019-10-31 14:26
 * Description: 申请售后
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.controller;

import com.unifig.mall.bean.model.OmsOrderAfterSale;
import com.unifig.mall.service.OmsOrderAfterSaleService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <h3>概要:</h3><p>OmsOrderAfterSaleController</p>
 * <h3>功能:</h3><p>申请售后</p>
 *
 * @create 2019-10-31
 * @since 1.0.0
 */
@RestController
@RequestMapping("/omsOrderAfterSale")
@Api(tags = "售后",description = "OmsOrderAfterSaleController")
@Slf4j
public class OmsOrderAfterSaleController {

    @Autowired
    private OmsOrderAfterSaleService omsOrderAfterSaleService;

    @ApiOperation("小程序申请退货")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultData create(@RequestBody OmsOrderAfterSale returnApply) {
        return omsOrderAfterSaleService.create(returnApply);
    }

    @ApiOperation("售后列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResultData list( @ApiParam("当前页")@RequestParam(required = false, defaultValue = "0") Integer page,
                            @ApiParam("每页条数")@RequestParam(required = false, defaultValue = "10") Integer rows,
                             @ApiParam("申请时间")@RequestParam(required = false, name = "createTime") String createTime,
                            @ApiParam("处理时间")@RequestParam(required = false, name = "updateTime") String updateTime,
                            @ApiParam("状态 0 ->已处理;1->已响应 ;2->已退款 ;3->已拒绝") @RequestParam(required = false, name = "status") Integer status) {
        return omsOrderAfterSaleService.list(page,rows,createTime, updateTime, status);
    }


    @ApiOperation("响应售后")
    @RequestMapping(value = "/answer", method = RequestMethod.POST)
    public ResultData answer( @ApiParam("售后订单id")@RequestParam(required = false, name = "id") String id) {
        return omsOrderAfterSaleService.answer(id);
    }

    @ApiOperation("退款")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public ResultData refund( @ApiParam("售后订单id")@RequestParam(required = false, name = "id") String id,
                              @ApiParam("退款金额")@RequestParam(required = false, name = "id") String money,
                              @ApiParam("操作类型  0  退款  1 拒绝退款")@RequestParam(required = false, name = "type") String type) {
        return omsOrderAfterSaleService.refund(id,money,type);
    }






}
