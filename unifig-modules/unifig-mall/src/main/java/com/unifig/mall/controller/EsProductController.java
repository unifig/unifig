package com.unifig.mall.controller;

import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.domain.EsProduct;
import com.unifig.mall.bean.domain.EsProductRelatedInfo;
import com.unifig.mall.service.EsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 搜索商品管理Controller
 * Created by   on 2018/01/22
 */
@Controller
@Api(tags = "搜索商品管理", description = "EsProductController")
@RequestMapping("/esProduct")
@Deprecated
//@ApiIgnore
public class EsProductController {
    @Autowired
    private EsProductService esProductService;

    @ApiOperation(value = "导入所有数据库中商品到ES")
    @RequestMapping(value = "/importAll", method = RequestMethod.POST)
    @ResponseBody
    public Object importAllList() {
        int count = esProductService.importAll();
        return new CommonResult().success(count);
    }

    @ApiOperation(value = "清空数据库中商品")
    @RequestMapping(value = "/deleteAll", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteAll() {
        esProductService.delete();
        return new CommonResult().success(true);
    }

    @ApiOperation(value = "根据id删除商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@ApiParam("id")  @PathVariable Long id) {
        esProductService.delete(id);
        return new CommonResult().success(null);
    }

    @ApiOperation(value = "根据id批量删除商品")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@ApiParam("id集合") @RequestParam("ids") List<Long> ids) {
        esProductService.delete(ids);
        return new CommonResult().success(null);
    }

    @ApiOperation(value = "根据id创建商品")
    @RequestMapping(value = "/create/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@ApiParam("商品id") @PathVariable Long id) {
        EsProduct esProduct = esProductService.create(id);
        if (esProduct != null) {
            return new CommonResult().success(esProduct);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation(value = "根据id更新商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object update(@ApiParam("商品id") @PathVariable Long id) {
        EsProduct esProduct = esProductService.update(id);
        if (esProduct != null) {
            return new CommonResult().success(esProduct);
        } else {
            return new CommonResult().failed();
        }
    }



    @ApiOperation(value = "简单搜索")
    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public Object search(@ApiParam("关键字搜索")   @RequestParam(required = false) String keyword,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.search(keyword, pageNum, pageSize);
        return new CommonResult().pageSuccess(esProductPage);
    }

    /**
     * 根据属性获取商品列表
     * @param recommandStatus  推荐状态；0->不推荐；1->推荐
     * @param type  商品类型 1 普通商品  2 积分商品
     * @param id  根据id获取详情
     * @param newStatus 新品状态:0->不是新品；1->新品
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取商品列表",notes = "id：商品id，pic：商品图，name：商品名称，price：商品价格，sale：销量,type：类型,usePointLimit:积分")
    @RequestMapping(value = "/search/list", method = RequestMethod.GET)
    @ResponseBody
    public Object searchList(@ApiParam("推荐状态；0->不推荐；1->推荐")  @RequestParam(required = false) Integer recommandStatus,
                             @ApiParam("商品类型 1 普通商品  2 积分商品") @RequestParam(required = false) Integer type,
                             @ApiParam("根据id获取详情") @RequestParam(required = false) Long id,
                             @ApiParam("新品状态:0->不是新品；1->新品") @RequestParam(required = false) Integer newStatus,
                             @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.search(recommandStatus,type,id,newStatus,pageNum, pageSize);
        return new CommonResult().pageSuccess(esProductPage);
    }

  /*  @ApiOperation(value = "综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Object search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Long brandId,
                         @RequestParam(required = false) Long productCategoryId,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                         @RequestParam(required = false, defaultValue = "0") Integer sort) {
        Page<EsProduct> esProductPage = esProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort);
        return new CommonResult().pageSuccess(esProductPage);
    }*/

    @ApiOperation(value = "根据商品id推荐商品")
    @RequestMapping(value = "/recommend/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Object recommend(@PathVariable Long id,
                            @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                            @RequestParam(required = false, defaultValue = "5") Integer pageSize){
        Page<EsProduct> esProductPage = esProductService.recommend(id, pageNum, pageSize);
        return new CommonResult().pageSuccess(esProductPage);
    }

    @ApiOperation(value = "获取搜索的相关品牌、分类及筛选属性")
    @RequestMapping(value = "/search/relate",method = RequestMethod.GET)
    @ResponseBody
    public Object searchRelatedInfo(@RequestParam(required = false) String keyword){
        EsProductRelatedInfo productRelatedInfo = esProductService.searchRelatedInfo(keyword);
        return new CommonResult().success(productRelatedInfo);
    }
}
