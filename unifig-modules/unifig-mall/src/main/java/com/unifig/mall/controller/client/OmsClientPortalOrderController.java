package com.unifig.mall.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.domain.ConfirmOrderResult;
import com.unifig.mall.bean.domain.OrderParam;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.service.OmsCartItemService;
import com.unifig.mall.service.OmsPortalOrderService;
import com.unifig.model.CartPromotionItem;
import com.unifig.result.Rest;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 订单管理Controller
 * Created by   on 2019-07-02
 */
@RestController
@Api(tags = "订单管理",description = "OmsPortalOrderController")
@RequestMapping("/client/order")
public class OmsClientPortalOrderController {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @ApiModelProperty
    private OmsCartItemService omsCartItemService;

    @ApiOperation("根据购物车信息生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrder",method = RequestMethod.POST)
    @ResponseBody
    public Object generateConfirmOrder(@CurrentUser UserCache userCache){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder();
        return new CommonResult().success(confirmOrderResult);
    }

    /**
     * 普通商品生成确认订单
     * @param ids
     * @return
     */
    @ApiOperation("根据购物车id生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrderByIds",method = RequestMethod.GET)
    @ResponseBody
    public ResultData<ConfirmOrderResult> generateConfirmOrderByIds(@ApiParam("购物车id数组") @RequestParam("ids") List<Long> ids){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderByIds(ids);
        return ResultData.result(true).setData(confirmOrderResult);
    }

    @ApiOperation("生成确认单信息(团购)")
    @RequestMapping(value = "/generateConfirmOrderOne",method = RequestMethod.POST)
    @ResponseBody
    public ResultData<ConfirmOrderResult> generateConfirmOrderOne(@RequestBody CartPromotionItem cartPromotionItem){
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderOne(cartPromotionItem);
        return ResultData.result(true).setData(confirmOrderResult);
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
    public ResultData generateOrderOne(@RequestBody OrderParam orderParam,@CurrentUser UserCache userCache){
        return portalOrderService.generateOrderOne(orderParam,userCache);
    }
//    @ApiOperation("支付成功的回调")
//    @RequestMapping(value = "/paySuccess",method = RequestMethod.POST)
//    @ResponseBody
//    public Object paySuccess(@RequestParam Long orderId){
//        return portalOrderService.paySuccess(orderId);
//    }

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


    @ApiOperation("生成确认单信息单个商品")
    @RequestMapping(value = "/generateConfirmOrderSingle",method = RequestMethod.POST)
    public Rest<ConfirmOrderResult> generateConfirmOrderSingle(@CurrentUser UserCache userCache,
                                           @ApiParam("商品id") @RequestParam String id,
                                           @ApiParam("数量") @RequestParam String quantity,
                                           @ApiParam("skuId") @RequestParam(required = false) String skuId
    ){
        //TODO 完善生成确认订单接口
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrderSingle(userCache,id,quantity,skuId);
        return Rest.resultData(new ConfirmOrderResult()).setData(confirmOrderResult);
    }



}
