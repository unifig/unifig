package com.unifig.organ.controller.client;


import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.domain.EsShop;
import com.unifig.organ.service.OmsShopService;
import com.unifig.organ.service.OmsShopStaffService;
import com.unifig.result.Rest;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 店铺客户端 控制器
 * </p>
 *
 *
 * @since 2019-03-11
 */
@RestController
@RequestMapping("/oms/client/shop")
@Api(tags = "店铺", description = "OmsShopClientController")
@ApiIgnore
public class OmsShopClientController {

    @Autowired
    private OmsShopService omsShopService;

    @Autowired
    private OmsShopStaffService omsShopStaffService;

    @ApiOperation(value = "店铺列表es")
    @GetMapping(value = "/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", required = false, dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "rows", value = "每页数量", required = false, dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "terraceId", value = "平台id", required = false, dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "搜索关键字  (店铺名称,店铺地址)", required = false, dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "id", value = "店铺id", required = false, dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "longitude", value = "经度", required = true, dataType = "BigDecimal",paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "维度", required = true, dataType = "BigDecimal",paramType = "query"),
            @ApiImplicitParam(name = "distance", value = "距离 单位 KM", required = false, dataType = "BigDecimal",paramType = "query")
    })
    public RestList<EsShop> EsSelectShopList(@RequestParam(required = false, defaultValue = "0") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer rows,
                                             @RequestParam(required = false ,defaultValue = "1") String terraceId,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = true) BigDecimal longitude,
                                             @RequestParam(required = true) BigDecimal latitude,
                                             @RequestParam(required = false, defaultValue = "500000") BigDecimal distance
    ) {
        if(page > 0){
            page = page-1;
        }
        Page<EsShop> pageList =  omsShopService.EsSelectShopList(page,rows,terraceId,keyword,longitude,latitude,distance);
        return RestList.resultData(new EsShop()).setData(pageList.getRecords()).setCount((int)pageList.getTotal());
    }

    @ApiOperation(value = "店铺距离")
    @GetMapping(value = "/shopDistance")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "longitude", value = "经度", required = true, dataType = "BigDecimal",paramType = "query"),
            @ApiImplicitParam(name = "latitude", value = "维度", required = true, dataType = "BigDecimal",paramType = "query")
    })
    public Map<String,String> shopDistance(@RequestParam(required = true) BigDecimal longitude,
                                           @RequestParam(required = true) BigDecimal latitude,
                                           @RequestParam(required = false, defaultValue = "5000") BigDecimal distance
    ) {
        return omsShopService.shopDistance(longitude,latitude,distance);
    }

    @ApiOperation(value = "导入所有店铺到es")
    @GetMapping(value = "/es/import/all")
    public ResultData importAll() {
        return omsShopService.importAll();
    }

    @ApiOperation(value = "查看详情")
    @GetMapping(value = "/selectById")
    public Rest<EsShop> selectById(@ApiParam("店铺id") @RequestParam(required = true) String id,
                                   @ApiParam("经度")@RequestParam(required = true) BigDecimal longitude,
                                   @ApiParam("纬度") @RequestParam(required = true) BigDecimal latitude) {
        return omsShopService.selectESById(id,longitude,latitude);
    }

    @ApiOperation(value = "根据用户id获取店铺id")
    @GetMapping(value = "/selectShopId/{id}")
    public String selectShopId(@PathVariable(value="id") String id) {
        String shopId = omsShopStaffService.selectByUserId(id);
        if (shopId != null){
            return shopId;
        }
        return "0";
    }
}

