package com.unifig.mall.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.OmsCartItem;
import com.unifig.mall.bean.domain.CartProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车管理Service
 *    on 2018/8/2.
 */
public interface OmsCartItemService {
    /**
     * 查询购物车中是否包含该商品，有增加数量，无添加到购物车
     */
    @Transactional
    int add(OmsCartItem cartItem,UserCache user);

    @Transactional
    int addTemp(OmsCartItem cartItem,UserCache user);

    /**
     * 根据会员编号获取购物车列表
     */
    List<OmsCartItem> list(Long memberId);

    /**
     * 获取包含促销活动信息的购物车列表
     */
    List<CartPromotionItem> listPromotion(Long memberId);

    List<CartPromotionItem> listPromotionByIds(List<Long> ids);

    /**
     * 修改某个购物车商品的数量
     */
    int updateQuantity(Long id, Long memberId, Integer quantity);

    /**
     * 批量删除购物车中的商品
     */
    int delete(Long memberId,List<Long> ids);

    /**
     *获取购物车中用于选择商品规格的商品信息
     */
    CartProduct getCartProduct(Long productId);

    /**
     * 修改购物车中商品的规格
     */
    @Transactional
    int updateAttr(OmsCartItem cartItem,UserCache user);

    /**
     * 清空购物车
     */
    int clear(Long memberId);

    /**
     *
     * @param ids
     * @return
     */
    List<OmsCartItem> listByIds(List<Long> ids);

    List<OmsCartItem> selectListById(List<Long> id);
}
