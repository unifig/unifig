package com.unifig.mall.dao;

import com.unifig.mall.bean.domain.PromotionProduct;
import com.unifig.mall.bean.domain.CartProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台系统自定义商品Dao
 *    on 2018/8/2.
 */
public interface PortalProductDao {
    CartProduct getCartProduct(@Param("id") Long id);
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);
}
