package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.domain.UmsMemberCollectionProduct;
import com.unifig.organ.service.UmsCollectionProductService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员收藏管理Controller
 *    on 2018/8/2.
 */
@RestController
@Api(tags = "会员商品收藏")
@RequestMapping("/ums/product/collection")
public class UmsCollectionProductController {
    @Autowired
    private UmsCollectionProductService memberCollectionService;

    @ApiOperation("添加商品收藏")
    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberCollectionProduct> addProduct(@RequestBody UmsMemberCollectionProduct productCollection, @CurrentUser UserCache userCache) {
        productCollection.setMemberId(Long.valueOf(userCache.getUserId()));
        productCollection.setCreateTime(new Date());
        int count = memberCollectionService.addProduct(productCollection);
        if (count > 0) {
            return ResultData.result(true).setData(productCollection);
        } else {
            return ResultData.result(false).setData(productCollection);
        }
    }

    @ApiOperation("删除收藏商品")
    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.POST)
    @ResponseBody
    public ResultData<UmsMemberCollectionProduct> deleteProduct(@CurrentUser UserCache userCache,@PathVariable Long productId) {
        int count = memberCollectionService.deleteProduct(Long.valueOf(userCache.getUserId()), productId);
        if (count > 0) {
            return ResultData.result(true).setData(productId);
        } else {
            return ResultData.result(false).setData(productId);
        }
    }

    @ApiOperation("收藏列表")
    @RequestMapping(value = "/listProduct", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberCollectionProduct> listProduct(@CurrentUser UserCache userCache) {
        List<UmsMemberCollectionProduct> memberProductCollectionList = memberCollectionService.listProduct(Long.valueOf(userCache.getUserId()));
        return ResultData.result(true).setData(memberProductCollectionList);
    }


    @ApiOperation("显示首页列表")
    @RequestMapping(value = "/listProduct/index", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<UmsMemberCollectionProduct> listProductIndex(@CurrentUser UserCache userCache) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<UmsMemberCollectionProduct> memberProductCollectionList = memberCollectionService.listProductIndex(Long.valueOf(userCache.getUserId()));
        long count = memberCollectionService.countProduct(Long.valueOf(userCache.getUserId()));
        result.put("list", memberProductCollectionList);
        result.put("count", count);
        return ResultData.result(true).setData(result);

    }

    @ApiOperation("判断用户是否收藏改商品")
    @RequestMapping(value = "/select/product/{id}", method = RequestMethod.GET)
    @ResponseBody
    public boolean selectByProductId(@PathVariable Long id,@CurrentUser UserCache userCache) {
        UmsMemberCollectionProduct umsMemberCollectionProduct = memberCollectionService.selectByProductId(id,Long.valueOf(userCache.getUserId()));
        if (umsMemberCollectionProduct!=null){
            return true;
        }
        return false;
    }


}
