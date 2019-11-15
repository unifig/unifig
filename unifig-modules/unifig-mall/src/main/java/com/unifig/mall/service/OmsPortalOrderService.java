package com.unifig.mall.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.ConfirmOrderResult;
import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.domain.OrderParam;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.model.CartPromotionItem;
import com.unifig.result.ResultData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 前台订单管理Service
 *    on 2018/8/30.
 */
public interface OmsPortalOrderService {
    /**
     * 根据用户购物车信息生成确认单信息
     */
    ConfirmOrderResult generateConfirmOrder();

    ConfirmOrderResult generateConfirmOrderByIds(List<Long> ids);

    /**
     * 根据提交信息生成订单
     */
    @Transactional
    CommonResult generateOrder(OrderParam orderParam,UserCache userCache);

    /**
     * 支付成功后的回调
     */
    @Transactional
    CommonResult paySuccess(Long orderId);

    /**
     * 自动取消超时订单
     */
    @Transactional
    CommonResult cancelTimeOutOrder();

    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(Long orderId);

    /**
     * 发送延迟消息取消订单
     */
    void sendDelayMessageCancelOrder(Long orderId);

    OmsOrder getOrderById(Long orderId);

    ConfirmOrderResult generateConfirmOrderOne(CartPromotionItem cartPromotionItem);

    ResultData generateOrderOne(OrderParam orderParam, UserCache userCache);

    ConfirmOrderResult generateConfirmOrderSingle(UserCache userCache, String id, String quantity, String skuId);
}
