package com.unifig.mall.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.dto.CommonResult;
import com.unifig.mall.bean.dto.PmsProductParam;
import com.unifig.mall.bean.dto.PmsProductQueryParam;
import com.unifig.mall.bean.dto.PmsProductResult;
import com.unifig.mall.bean.model.PmsProduct;
import com.unifig.mall.bean.vo.HomeSmsAdvertiseVo;
import com.unifig.mall.bean.vo.PmsProductVo;
import com.unifig.mall.feign.OmsShopClientFeign;
import com.unifig.mall.feign.UmsProductCollectionFeign;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsProductService;
import com.unifig.mall.service.SmsHomeAdvertiseService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理Controller
 *    on 2018/4/26.
 */
@RestController
@Api(tags = "商品管理", description = "PmsClientProductController")
@RequestMapping("/client/product")
public class PmsClientProductController {

    @Autowired
    private PmsProductService productService;

    @Autowired
    private SmsHomeAdvertiseService advertiseService;


    @ApiOperation("根据商品id获取商品详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultData<PmsProductVo> selectById(@PathVariable Long id) {
        ResultData productResult = productService.selectById(id);
        return ResultData.result(true).setData(productResult.getData());
    }


    @ApiOperation("根据商品id获取商品详情")
    @RequestMapping(value = "/orcode/{id}", method = RequestMethod.GET)
    public ResultData<PmsProductVo> orcodeSelectById(@PathVariable Long id) {
        ResultData productResult = productService.orcodeSelectById(id);
        return ResultData.result(true).setData(productResult.getData());
    }

    @ApiOperation("获取商城页面轮播图")
    @RequestMapping(value = "/advertise", method = RequestMethod.GET)
    public ResultData<HomeSmsAdvertiseVo> getAdvertise() {
        List<HomeSmsAdvertiseVo> homeSmsAdvertiseVos = advertiseService.selectHomeListByType(new Date(System.currentTimeMillis()), Constants.DEFAULT_VAULE_THREE);
        return ResultData.result(true).setData(homeSmsAdvertiseVos);
    }


}
