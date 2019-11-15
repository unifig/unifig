package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.annotation.CurrentUser;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.LmsShipper;
import com.unifig.mall.service.LmsShipperService;
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
@Api(tags = "物流公司", description = "物流公司")
@RestController
@RequestMapping("/lms/shipper")
@ApiIgnore
public class LmsShipperController {
    @Autowired
    private LmsShipperService lmsShipperService;

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
            EntityWrapper<LmsShipper> lmsShipperWrapper = new EntityWrapper<LmsShipper>();
            lmsShipperWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<LmsShipper> lmsShipperPage = lmsShipperService.selectPage(new Page<LmsShipper>(page, rows), lmsShipperWrapper);
            int count = lmsShipperService.selectCount(lmsShipperWrapper);
            return ResultData.result(true).setData(lmsShipperPage.getRecords()).setCount(count);
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
            LmsShipper lmsShipper = lmsShipperService.selectById(String.valueOf(id));
            return ResultData.result(true).setData(lmsShipper);
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
    public ResultData iou(@RequestBody LmsShipper lmsShipper) {
        try {
            if (lmsShipper == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            //补充其他数据
            String id = lmsShipper.getId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (id == null) {
                lmsShipper.setCreateTime(now);
            }
            lmsShipper.setEditTime(now);
            lmsShipper.setEnable(Constants.DEFAULT_VAULE_ONE);
            lmsShipperService.insertOrUpdate(lmsShipper);
            return ResultData.result(true).setData(lmsShipper);
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
            LmsShipper lmsShipper = lmsShipperService.selectById(String.valueOf(id));
            if (lmsShipper == null)
                return ResultData.result(false).setCode(MsgConstants.DATA_IS_NULL);
            lmsShipper.setEnable(Constants.DEFAULT_VAULE_ZERO);
            //更新状态
            lmsShipperService.updateById(lmsShipper);
            return ResultData.result(true);
        } catch (Exception e) {
            return ResultData.result(false);

        }

    }
}

