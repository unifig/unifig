package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.domain.ConfirmOrderResult;
import com.unifig.mall.bean.domain.OrderParam;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.service.OmsPortalOrderService;
import com.unifig.model.CartPromotionItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 订单管理Controller
 *    on 2018/8/30.
 */
@RestController
@Api(tags = "订单管理",description = "OmsPortalOrderController")
@RequestMapping("/order")
@ApiIgnore
public class OmsPortalOrderController {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @ApiOperation("根据购物车信息生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrder",method = RequestMethod.POST)
    public Object generateConfirmOrder(@CurrentUser UserCache userCache){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder();
        return new CommonResult().success(confirmOrderResult);
    }

    @ApiOperation("根据购物车id生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrderByIds",method = RequestMethod.GET)
    public Object generateConfirmOrderByIds(@ApiParam("购物车id数组") @RequestParam("ids") List<Long> ids){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderByIds(ids);
        return new CommonResult().success(confirmOrderResult);
    }

    @ApiOperation("生成确认单信息(团购)")
    @RequestMapping(value = "/generateConfirmOrderOne",method = RequestMethod.POST)
    @ResponseBody
    public Object generateConfirmOrderOne(@RequestBody CartPromotionItem cartPromotionItem){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderOne(cartPromotionItem);
        return new CommonResult().success(confirmOrderResult);
    }

    @ApiOperation("根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder",method = RequestMethod.POST)
    @ResponseBody
    public Object generateOrder(@RequestBody OrderParam orderParam,@CurrentUser UserCache userCache){
        return portalOrderService.generateOrder(orderParam,userCache);
    }

    @ApiOperation("团购生成订单")
    @RequestMapping(value = "/generateOrderOne",method = RequestMethod.POST)
    @ResponseBody
    public Object generateOrderOne(@RequestBody OrderParam orderParam,@CurrentUser UserCache userCache){
        return portalOrderService.generateOrderOne(orderParam,userCache);
    }
    @ApiOperation("支付成功的回调")
    @RequestMapping(value = "/paySuccess",method = RequestMethod.POST)
    @ResponseBody
    public Object paySuccess(@RequestParam Long orderId){
        return portalOrderService.paySuccess(orderId);
    }

    @ApiOperation("自动取消超时订单")
    @RequestMapping(value = "/cancelTimeOutOrder",method = RequestMethod.POST)
    @ResponseBody
    public Object cancelTimeOutOrder(){
        return portalOrderService.cancelTimeOutOrder();
    }

    @ApiOperation("取消单个超时订单")
    @RequestMapping(value = "/cancelOrder",method = RequestMethod.POST)
    @ResponseBody
    public Object cancelOrder(Long orderId){
        portalOrderService.sendDelayMessageCancelOrder(orderId);
        return new CommonResult().success(null);
    }

    @ApiOperation("获取订单详情")
    @RequestMapping(value = "/getOrderById/{orderId}",method = RequestMethod.GET)
    @ResponseBody
    public Object getOrderById(@ApiParam("订单id") @PathVariable Long orderId){
        OmsOrder order = portalOrderService.getOrderById(orderId);
        return new CommonResult().success(order);
    }


//    @ApiOperation("生成确认单信息单个商品")
//    @RequestMapping(value = "/generateConfirmOrderOne",method = RequestMethod.POST)
//    @ResponseBody
//    public Object generateConfirmOrderOne(@RequestBody CartPromotionItem cartPromotionItem){
//        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderOne(cartPromotionItem);
//        return new CommonResult().success(confirmOrderResult);
//    }
}
