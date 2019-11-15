package com.unifig.mall.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.result.ResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信支付Service
 */
public interface OmsPayService {

    /**
     * 统一下单(获取支付的请求参数)
     * @param user
     * @param orderId
     * @param request
     * @return
     */
    ResultData payPrepay(UserCache user, Long orderId, HttpServletRequest request);


    /**
     * 订单查询
     * @param user
     * @param orderId
     * @return
     */
    ResultData orderQuery(UserCache user,Long orderId);

    /**
     * 退款操作
     * @param orderId
     * @param refundMoney
     * @return
     */
    ResultData refund(Long orderId, Double refundMoney);

    /**
     * 微信回调
     * @param request
     * @param response
     */
    void notify(HttpServletRequest request, HttpServletResponse response);

}
