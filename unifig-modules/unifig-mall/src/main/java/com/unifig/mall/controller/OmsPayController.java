package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.mapper.OmsOrderItemMapper;
import com.unifig.mall.mapper.OmsOrderMapper;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderExample;
import com.unifig.mall.bean.model.OmsOrderItem;
import com.unifig.mall.bean.model.OmsOrderItemExample;
import com.unifig.mall.service.OmsPayService;
import com.unifig.mall.service.OmsPortalOrderService;
import com.unifig.result.ResultData;
import com.unifig.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 作者:   <br>
 * 时间: 2019-01-26 08:32<br>
 * 描述: OmsPayController <br>
 */
@Api(tags = "商户支付")
@RestController
@RequestMapping("/pay")
@Slf4j
public class OmsPayController  {

    @Autowired
    private OmsPayService omsPayService;


    /**
     * 获取支付的请求参数
     */
    @ApiOperation(value = "获取支付的请求参数")
    @RequestMapping(value = "prepay",method = RequestMethod.GET)
    public ResultData payPrepay(@CurrentUser UserCache user, @ApiParam("订单id")@RequestParam Long orderId, HttpServletRequest request) {
        return omsPayService.payPrepay(user,orderId,request);
    }



    /**
     * 微信查询订单状态
     */
    @ApiOperation(value = "查询订单状态")
    @PostMapping("query")
    public Object orderQuery(@CurrentUser UserCache userCache,@ApiParam("订单id") Long orderId) {
        return omsPayService.orderQuery(userCache,orderId);
    }

    /**
     * 微信订单回调接口
     *
     * @return
     */
    @ApiOperation(value = "微信订单回调接口")
    @RequestMapping(value = "/notify", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        omsPayService.notify(request,response);
    }


    /**
     * 订单退款请求
     */
    @ApiOperation(value = "订单退款请求")
    @RequestMapping(value = "refund",method = RequestMethod.GET)
    public ResultData refund(@ApiParam("订单id") @RequestParam Long orderId,@ApiParam("退款金额") @RequestParam(required = false) Double refundMoney) {
        return omsPayService.refund(orderId, refundMoney);
    }


}