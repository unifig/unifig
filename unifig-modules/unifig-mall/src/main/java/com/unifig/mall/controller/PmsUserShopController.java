package com.unifig.mall.controller;


import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.PmsUserShop;
import com.unifig.mall.service.PmsUserShopService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 我的店铺列表 前端控制器
 * </p>
 *
 *
 * @since 2019-02-19
 */
@RestController
@RequestMapping("/pmsUserShop")
@Api(tags = "PmsUserShopController", description = "我的店铺")
@ApiIgnore
public class PmsUserShopController {

    @Autowired
    private PmsUserShopService pmsUserShopService;


    /**
     * 选择代理商品
     * @param pmsUserShop
     * @return
     */
    @ApiOperation(value = "选择代理商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultData create(@ApiParam("选择代理商品") @RequestBody PmsUserShop pmsUserShop,@CurrentUser UserCache userCache) {
        pmsUserShop.setUserId(userCache.getUserId());
        PmsUserShop result = pmsUserShopService.create(pmsUserShop);
        if (result != null) {
            return ResultData.result(true);
        } else {
            return ResultData.result(false);
        }
    }

    /**
     * 移除代理商品
     * @return
     */
    @ApiOperation(value = "移除代理商品")
    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public ResultData remove(@ApiParam("移除代理商品")String id, @CurrentUser UserCache userCache) {
        PmsUserShop pmsUserShop = new PmsUserShop();
        pmsUserShop.setId(id);
        pmsUserShop.setUserId(userCache.getUserId());
        PmsUserShop result = pmsUserShopService.remove(pmsUserShop);
        if (result != null) {
            return ResultData.result(true);
        } else {
            return ResultData.result(false);
        }
    }

    /**
     * 获取总部代理商品列表
     * @return
     */
    @ApiOperation(value = "获取总部代理商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultData list(@RequestParam(required = false, defaultValue = "0") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer rows,@CurrentUser UserCache userCache) {
        ResultData result = pmsUserShopService.selectProductList(page,rows,userCache);
        
        return result;
    }

    /**
     * 我的代理商品列表
     * @return
     */
    @ApiOperation(value = "我的代理商品列表")
    @RequestMapping(value = "/myList", method = RequestMethod.GET)
    public ResultData myList(@RequestParam(required = false, defaultValue = "0") Integer page,
                           @RequestParam(required = false, defaultValue = "5") Integer rows,@CurrentUser UserCache userCache) {
        ResultData result = pmsUserShopService.selectMyProductList(page,rows,userCache);
        return result;
    }


}

