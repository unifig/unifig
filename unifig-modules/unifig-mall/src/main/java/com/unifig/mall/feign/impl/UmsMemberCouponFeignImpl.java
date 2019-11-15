package com.unifig.mall.feign.impl;

import com.unifig.mall.feign.UmsMemberCouponFeign;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCouponHistoryDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsMemberCouponFeignImpl implements UmsMemberCouponFeign {
    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type) {
        return null;
    }
}
