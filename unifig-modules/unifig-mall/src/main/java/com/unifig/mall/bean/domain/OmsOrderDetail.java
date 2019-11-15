package com.unifig.mall.bean.domain;

import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderItem;

import java.util.List;

/**
 * 包含订单商品信息的订单详情
 *    on 2018/9/4.
 */
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;

    public List<OmsOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OmsOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
