package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.annotation.CurrentUser;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.LmsOrder;
import com.unifig.mall.service.LmsOrderService;
import com.unifig.result.MsgConstants;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Timestamp;

/**
 * <p>
 * 物流订单表 前端控制器
 * </p>
 *
 *
 * @since 2019-02-15
 */
@Api(tags = "物流订单", description = "物流订单")
@RestController
@RequestMapping("/lms/order")
@ApiIgnore
public class LmsOrderController {

    @Autowired
    private LmsOrderService lmsOrderService;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表数据查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ResultData list(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "2") Integer rows, @CurrentUser UserCache userCache) {
        try {
            EntityWrapper<LmsOrder> lmsOrderWrapper = new EntityWrapper<LmsOrder>();
            lmsOrderWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<LmsOrder> lmsOrderPage = lmsOrderService.selectPage(new Page<LmsOrder>(page, rows), lmsOrderWrapper);
            int count = lmsOrderService.selectCount(lmsOrderWrapper);
            return ResultData.result(true).setData(lmsOrderPage.getRecords()).setCount(count);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    /**
     * 详情
     *
     * @return
     */
    @ApiOperation("详情数据查询")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    @ResponseBody
    public ResultData info(@RequestParam(required = true, defaultValue = "24") String id

    ) {
        try {
            LmsOrder lmsOrder = lmsOrderService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(lmsOrder);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

    /**
     * 新建 更新
     *
     * @return
     */
    @ApiOperation("新增或者更新")
    @PostMapping(value = "/iou")
    public ResultData iou(@RequestBody LmsOrder lmsOrder) {
        try {
            if (lmsOrder == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = lmsOrder.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                lmsOrder.setCreateTime(now);
            }
            lmsOrder.setEditTime(now);
            lmsOrder.setEnable(Constants.DEFAULT_VAULE_ONE);
            lmsOrderService.insertOrUpdate(lmsOrder);
            return ResultData.result(true).setData(lmsOrder);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }


    /**
     * 删除
     *
     * @return
     */
    @ApiOperation("删除")
    @RequestMapping(value = "del", method = RequestMethod.GET)
    public ResultData del(@RequestParam String id
    ) {
        try {
            LmsOrder lmsOrder = lmsOrderService.selectById(String.valueOf(id));
            if (lmsOrder == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            lmsOrder.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            lmsOrderService.updateById(lmsOrder);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }

    /**
     * 运费查询
     *
     * @return
     */
    @ApiOperation("运费查询")
    @RequestMapping(value = "freight", method = RequestMethod.GET)
    @ResponseBody
    public ResultData freight(@RequestParam(required = true, defaultValue = "24") String fromAddressId,@RequestParam(required = true, defaultValue = "24") String toIAddressd

    ) {
        try {
            lmsOrderService.selectFreight(fromAddressId,toIAddressd);
            return ResultData.result(true).setData(15);
        } catch (Exception e) {
            return ResultData.result(false);
        }

    }

}

