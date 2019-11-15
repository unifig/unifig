package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.dto.PmsProductParam;
import com.unifig.mall.bean.dto.CommonResult;
import com.unifig.mall.bean.dto.PmsProductQueryParam;
import com.unifig.mall.bean.dto.PmsProductResult;
import com.unifig.mall.bean.model.PmsProduct;
import com.unifig.mall.feign.OmsShopClientFeign;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsProductService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理Controller
 *    on 2018/4/26.
 */
@Controller
@Api(tags = "商品管理", description = "PmsProductController")
@RequestMapping("/product")
@ApiIgnore
public class PmsProductController {
    @Autowired
    private PmsProductService productService;

    @Autowired
    private EsProductService esProductService;

    @Autowired
    private OmsShopClientFeign omsShopClientFeign;

    @ApiOperation("创建商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestBody PmsProductParam productParam,@CurrentUser UserCache user) {
        //检查店铺字段是否为空,店铺为空默认添加单前登录用户店铺商品
        if(productParam.getShopId() == null){
            //获取用户登录店铺id
            omsShopClientFeign.selectShopId(Long.valueOf(user.getUserId()));
            productParam.setShopId(omsShopClientFeign.selectShopId(Long.valueOf(user.getUserId())));
        }
        int count = productService.create(productParam);
        if (count > 0) {
            //更新商品到es
            esProductService.create(productParam.getId());
            return new CommonResult().success(productParam.getId());
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("根据商品id获取商品编辑信息")
    @RequestMapping(value = "/updateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:read')")
    public Object getUpdateInfo(@PathVariable Long id) {
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return new CommonResult().success(productResult);
    }

    @ApiOperation("根据商品id获取商品详情")
    @RequestMapping(value = "/selectById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object selectById(@PathVariable Long id) {
        ResultData productResult = productService.selectById(id);
        return productResult;
    }

    @ApiOperation("更新商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:update')")
    public Object update(@PathVariable Long id, @RequestBody PmsProductParam productParam, BindingResult bindingResult) {
        int count = productService.update(id, productParam);
        if (count > 0) {
            //更新商品到es
            esProductService.update(id);
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("查询商品")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object getList(PmsProductQueryParam productQueryParam,
                       @RequestParam(value = "pageSize", defaultValue = "1000") Integer pageSize,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          String type,
                          @ApiParam("id")   Long id,
                          @CurrentUser UserCache user) {

        /*if(productQueryParam.getShopId() == null){
            //获取用户登录店铺id
            omsShopClientFeign.selectShopId(Long.valueOf(user.getUserId()));
            productQueryParam.setShopId(omsShopClientFeign.selectShopId(Long.valueOf(user.getUserId())));
        }*/
        List<PmsProduct> productList = null;
        productList = productService.list(productQueryParam, pageSize, pageNum);
        if(type != null && !type.equals("")){
            productList = productList.stream().filter(pmsProduct -> pmsProduct.getType().equals(type)).collect(Collectors.toList());
        }
        if(id != null && !id.equals("")){
            productList = productList.stream().filter(pmsProduct -> pmsProduct.getId().equals(id)).collect(Collectors.toList());
        }
        return new CommonResult().pageSuccess(productList);
    }

    @ApiOperation("根据商品名称或货号模糊查询")
    @RequestMapping(value = "/simpleList", method = RequestMethod.GET)
    @ResponseBody
    public Object getList(String  keyword) {
        List<PmsProduct> productList = productService.list(keyword);
        return new CommonResult().success(productList);
    }

    @ApiOperation("批量修改审核状态")
    @RequestMapping(value = "/update/verifyStatus",method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:update')")
    public Object updateVerifyStatus(@RequestParam("ids") List<Long> ids,
                                     @RequestParam("verifyStatus") Integer verifyStatus,
                                     @RequestParam("detail") String detail) {
        int count = productService.updateVerifyStatus(ids, verifyStatus, detail);
        if (count > 0) {
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("批量上下架")
    @RequestMapping(value = "/update/publishStatus",method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:update')")
    public Object updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                     @RequestParam("publishStatus") Integer publishStatus) {
        int count = productService.updatePublishStatus(ids, publishStatus);
        if (count > 0) {
            for (Long id : ids) {
                esProductService.update(id);
            }
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("批量推荐商品")
    @RequestMapping(value = "/update/recommendStatus",method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:update')")
    public Object updateRecommendStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("recommendStatus") Integer recommendStatus) {
        int count = productService.updateRecommendStatus(ids, recommendStatus);
        if (count > 0) {
            for (Long id : ids) {
                esProductService.update(id);
            }
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("批量设为新品")
    @RequestMapping(value = "/update/newStatus",method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:update')")
    public Object updateNewStatus(@RequestParam("ids") List<Long> ids,
                                        @RequestParam("newStatus") Integer newStatus) {
        int count = productService.updateNewStatus(ids, newStatus);
        if (count > 0) {
            for (Long id : ids) {
                esProductService.update(id);
            }
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }

    @ApiOperation("批量修改删除状态")
    @RequestMapping(value = "/update/deleteStatus",method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:product:delete')")
    public Object updateDeleteStatus(@RequestParam("ids") List<Long> ids,
                                  @RequestParam("deleteStatus") Integer deleteStatus) {
        int count = productService.updateDeleteStatus(ids, deleteStatus);
        if (count > 0) {
            for (Long id : ids) {
                esProductService.update(id);
            }
            return new CommonResult().success(count);
        } else {
            return new CommonResult().failed();
        }
    }
}
