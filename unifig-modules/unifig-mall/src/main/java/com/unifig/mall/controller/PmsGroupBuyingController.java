package com.unifig.mall.controller;
import	java.sql.Wrapper;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsGroupBuyingService;
import com.unifig.mall.service.PmsGroupBuyingUserService;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * Commodity group purchase basics
 * </p>
 *
 *
 * @since 2019-01-23
 */
@RestController
@RequestMapping("/pmsGroupBuying")
@Api(tags = "团购配置",description = "PmsGroupBuyingController")
public class PmsGroupBuyingController {

    @Autowired
    private PmsGroupBuyingService pmsGroupBuyingService;

    @Autowired
    private EsProductService esProductService;


    /**
     * 创建团购
     * @param pmsGroupBuying
     * @return
     */
    @ApiOperation(value = "创建团购")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultData create(@ApiParam("团购信息") @RequestBody PmsGroupBuying pmsGroupBuying) {
        EntityWrapper<PmsGroupBuying> wrapper = new EntityWrapper<PmsGroupBuying>();
        wrapper.eq("enable", Constants.DEFAULT_VAULE_ZERO);
        wrapper.eq("product_id", pmsGroupBuying.getProductId());
        //不包含已结束的
        wrapper.ne("status",2);
        List<PmsGroupBuying> pmsGroupBuyings = pmsGroupBuyingService.selectList(wrapper);
        if(pmsGroupBuyings != null && pmsGroupBuyings.size()>0){
            return ResultData.result(false).setMsg("改商品团购以存在请勿重复创建");
        }
        PmsGroupBuying groupBuying = pmsGroupBuyingService.create(pmsGroupBuying);
        if (groupBuying != null) {
            return ResultData.result(true);
        } else {
            return ResultData.result(false);
        }
    }

    /**
     * 修改团购
     * @param pmsGroupBuying
     * @return
     */
    @ApiOperation(value = "修改团购")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultData update(@ApiParam("团购信息") @RequestBody PmsGroupBuying pmsGroupBuying) {
        if(pmsGroupBuying == null || pmsGroupBuying.getId() == null){
            return ResultData.result(false).setMsg("团购id不能为空");
        }
        PmsGroupBuying groupBuying = pmsGroupBuyingService.update(pmsGroupBuying);
        if (groupBuying != null) {
            //Update merchandise to es
            esProductService.update(Long.parseLong(groupBuying.getProductId()));
            return ResultData.result(true);
        } else {
            return ResultData.result(false);
        }
    }

    /**
     * 团购配置列表
     * @return
     */
    @ApiOperation(value = "团购配置列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public RestList<PmsGroupBuying> list(@RequestParam(required = false, defaultValue = "0") Integer page,
                                     @RequestParam(required = false, defaultValue = "10") Integer rows,
                                     @ApiParam("状态") @RequestParam(required = false) Integer status) {
        EntityWrapper<PmsGroupBuying> wrapper = new EntityWrapper<PmsGroupBuying>();
        wrapper.eq("enable", Constants.DEFAULT_VAULE_ZERO);
        if(status != null)
            wrapper.eq("status",status);
        wrapper.orderBy("creation_time desc");
        Page<PmsGroupBuying> result = pmsGroupBuyingService.selectPage(new Page<PmsGroupBuying>(page, rows), wrapper);
        if(result != null){
            return  RestList.resultData(new PmsGroupBuying()).setCount(pmsGroupBuyingService.selectCount(wrapper)).setData(result.getRecords());
        }else{
            return  RestList.resultError();
        }
    }

}

