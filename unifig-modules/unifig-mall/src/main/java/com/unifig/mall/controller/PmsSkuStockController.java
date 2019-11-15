package com.unifig.mall.controller;

import com.unifig.mall.service.PmsSkuStockService;
import com.unifig.mall.bean.dto.CommonResult;
import com.unifig.mall.bean.model.PmsSkuStock;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * sku库存Controller
 *    on 2018/4/27.
 */
@Controller
@Api(tags = "sku商品库存管理", description = "PmsSkuStockController")
@RequestMapping("/sku")
public class PmsSkuStockController {
    @Autowired
    private PmsSkuStockService skuStockService;

    @ApiOperation("根据商品编号及编号模糊搜索sku库存")
    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<PmsSkuStock> getList(@PathVariable Long pid, @RequestParam(value = "keyword",required = false) String keyword) {
        List<PmsSkuStock> skuStockList = skuStockService.getList(pid, keyword);
        return ResultData.result(true).setData(skuStockList);
    }

    @ApiOperation("根据商品id和属性获取sku信息")
    @RequestMapping(value = "/selectById", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<PmsSkuStock> selectById(@RequestParam(value = "pid") Long pid, @RequestParam(value = "keyword",required = false) String keyword) {
        PmsSkuStock skuStockList = skuStockService.selectById(pid, keyword);
        return ResultData.result(true).setData(skuStockList);
    }

    @ApiOperation("批量更新库存信息")
    @RequestMapping(value ="/update/{pid}",method = RequestMethod.POST)
    @ResponseBody
    public ResultData<PmsSkuStock> update(@PathVariable Long pid,@RequestBody List<PmsSkuStock> skuStockList){
        int count = skuStockService.update(pid,skuStockList);
        if(count>0){
            return ResultData.result(true).setData(count);
        }else{
            return ResultData.result(false);
        }
    }
}
