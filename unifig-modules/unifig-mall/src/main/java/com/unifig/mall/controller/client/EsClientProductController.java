package com.unifig.mall.controller.client;

import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.domain.EsProduct;
import com.unifig.mall.bean.domain.EsProductRelatedInfo;
import com.unifig.mall.feign.OmsShopClientFeign;
import com.unifig.mall.service.EsProductService;
import com.unifig.result.RestList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 客户端搜索商品管理Controller
 * Created by   on 2018/01/22
 */
@Controller
@Api(tags = "搜索商品管理", description = "EsPlatProductController")
@RequestMapping("/client/esProduct")
public class EsClientProductController {
    @Autowired
    private EsProductService esProductService;

    @Autowired
    private OmsShopClientFeign omsShopClientFeign;

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
     * @param newStatus 新品状态:0->不是新品；1->新品
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取商品列表",notes = "id：商品id，pic：商品图，name：商品名称，price：商品价格，sale：销量,type：类型,usePointLimit:积分")
    @RequestMapping(value = "/search/list", method = RequestMethod.GET)
    @ResponseBody
    public RestList<EsProduct> searchList(@ApiParam("推荐状态；0->不推荐；1->推荐")  @RequestParam(required = false) Integer recommandStatus,
                               @ApiParam("商品类型 1 普通商品  2 积分商品  3 团购商品") @RequestParam(required = false) Integer type,
                               @ApiParam("平台Id") @RequestParam(required = false, defaultValue = "1") String terraceId,
                               @ApiParam("店铺Id") @RequestParam(required = false) String shopId,
                               @ApiParam("新品状态:0->不是新品；1->新品") @RequestParam(required = false) Integer newStatus,
                               @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                               @RequestParam(required = false, defaultValue = "50") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.search(recommandStatus,type,newStatus,pageNum, pageSize,terraceId,shopId);
        return RestList.resultData(new EsProduct()).setData(esProductPage.getContent()).setCount(esProductPage.getTotalPages());
    }

    @ApiOperation(value = "综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Object search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Long brandId,
                         @RequestParam(required = false) String productCategoryId,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                         @RequestParam(required = false, defaultValue = "0") Integer sort,
                         @RequestParam(required = false, defaultValue = "0") BigDecimal longitude,
                         @RequestParam(required = false, defaultValue = "0") BigDecimal latitude, HttpServletRequest request) {


        if(pageNum >0){
            pageNum =pageNum-1;
        }
        Page<EsProduct> esProductPage = esProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort);
        //判断经纬度是否为0  不为零进行查询
        if(longitude.compareTo(latitude) != 0){
            Map<String, String> stringStringMap = omsShopClientFeign.shopDistance(longitude, latitude);
            List<EsProduct> content = esProductPage.getContent();
            for (EsProduct esProduct : content) {
                esProduct.setDistance(stringStringMap.get(esProduct.getShopId()));
            }
            return new CommonResult().pageSuccesunifigData(esProductPage,content);
        }
        return new CommonResult().pageSuccess(esProductPage);
    }

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
