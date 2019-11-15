package com.unifig.mall.bean.dto;

import com.unifig.mall.bean.model.SmsFlashPromotionProductRelation;
import com.unifig.mall.bean.model.PmsProduct;
import lombok.Getter;
import lombok.Setter;

/**
 * 限时购及商品信息封装
 *    on 2018/11/16.
 */
public class SmsFlashPromotionProduct extends SmsFlashPromotionProductRelation {
    @Getter
    @Setter
    private PmsProduct product;
}
