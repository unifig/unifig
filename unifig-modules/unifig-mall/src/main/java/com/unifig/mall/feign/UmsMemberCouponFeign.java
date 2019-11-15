package com.unifig.mall.feign;

import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCouponHistoryDetail;

import java.util.List;

//@Component
//@FeignClient(name = "unifig-organ"/*,url = "localhost:8004"*/)
public interface UmsMemberCouponFeign {

    /**
     * 根据购物车信息获取可用优惠券
     */
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type);
}
