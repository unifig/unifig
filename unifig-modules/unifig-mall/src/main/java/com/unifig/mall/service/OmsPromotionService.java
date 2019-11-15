package com.unifig.mall.service;

import com.unifig.model.CartPromotionItem;
import com.unifig.model.OmsCartItem;

import java.util.List;

/**
 *    on 2018/8/27.
 * 促销管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     *
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
