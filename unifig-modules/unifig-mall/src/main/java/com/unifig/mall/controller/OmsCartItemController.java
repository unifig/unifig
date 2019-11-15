package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.feign.UmsMemberFeign;
import com.unifig.mall.service.OmsCartItemService;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.OmsCartItem;
import com.unifig.mall.bean.domain.CartProduct;
import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.utils.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

/**
 * 购物车管理Controller
 *    on 2018/8/2.
 */
@Controller
@Api(tags = "购物车管理", description = "OmsCartItemController")
@RequestMapping("/cart")
@Deprecated
@ApiIgnore
public class OmsCartItemController {
    @Autowired
    private OmsCartItemService cartItemService;

    @Autowired
    private UmsMemberFeign umsMemberFeign;

    @Autowired
    private UserTokenUtil userTokenUtil;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Object add(@RequestBody OmsCartItem cartItem,/*@RequestHeader(required = false) String Authorization*/@CurrentUser UserCache user) {
//        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        /*UserCache user=new UserCache();
        user.setUserId("1");
        user.setNickName(" ");*/
        int count = cartItemService.add(cartItem,user);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list(@CurrentUser UserCache user) {
        /*UserCache user=new UserCache();
        user.setUserId("1");
        user.setNickName(" ");*/
//        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        List<OmsCartItem> cartItemList = cartItemService.list(Long.parseLong(user.getUserId()));
        return new CommonResult().success(cartItemList);
    }

    @ApiOperation("获取某个会员的购物车列表,包括促销信息")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    public Object listPromotion(@RequestHeader(required = false) String Authorization) {
        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(Long.parseLong(user.getUserId()));
        return new CommonResult().success(cartPromotionItemList);
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.GET)
    @ResponseBody
    public Object updateQuantity(@ApiParam("购物车商品id") @RequestParam Long id,
                                 @ApiParam("库存") @RequestParam Integer quantity,
                                 @RequestHeader(required = false) String Authorization) {
        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        int count = cartItemService.updateQuantity(id,Long.parseLong(user.getUserId()),quantity);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getCartProduct(@ApiParam("商品id") @PathVariable Long productId) {
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return new CommonResult().success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public Object updateAttr(@RequestBody OmsCartItem cartItem,@RequestHeader(required = false) String Authorization) {
        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        int count = cartItemService.updateAttr(cartItem,user);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@ApiParam("购物车商品id") Long[] ids,
                         @RequestHeader(required = false) String Authorization) {
        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        int count = cartItemService.delete(Long.parseLong(user.getUserId()), Arrays.asList(ids));
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public Object clear(@RequestHeader(required = false) String Authorization) {
        UserCache user = userTokenUtil.getUserCacheFromToken(Authorization);
        int count = cartItemService.clear(Long.parseLong(user.getUserId()));
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }
}
