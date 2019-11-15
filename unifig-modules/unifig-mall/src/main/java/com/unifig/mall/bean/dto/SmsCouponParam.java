package com.unifig.mall.bean.dto;

import com.unifig.model.SmsCoupon;
import com.unifig.model.SmsCouponProductCategoryRelation;
import com.unifig.model.SmsCouponProductRelation;
import com.unifig.model.SmsCouponShopsRelation;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 优惠券信息封装，包括绑定商品和绑定分类
 *    on 2018/8/28.
 */
public class SmsCouponParam extends SmsCoupon {
    //优惠券绑定的商品
    @ApiModelProperty("优惠券绑定的商品")
    private List<SmsCouponProductRelation> productRelationList;
    //优惠券绑定的商品分类
    @ApiModelProperty("优惠券绑定的商品分类")
    private List<SmsCouponProductCategoryRelation> productCategoryRelationList;

    @ApiModelProperty("优惠券绑定的店铺")
    private List<SmsCouponShopsRelation> shopsRelationList;

    public List<SmsCouponShopsRelation> getShopsRelationList() {
        return shopsRelationList;
    }

    public void setShopsRelationList(List<SmsCouponShopsRelation> shopsRelationList) {
        this.shopsRelationList = shopsRelationList;
    }

    public List<SmsCouponProductRelation> getProductRelationList() {
        return productRelationList;
    }

    public void setProductRelationList(List<SmsCouponProductRelation> productRelationList) {
        this.productRelationList = productRelationList;
    }

    public List<SmsCouponProductCategoryRelation> getProductCategoryRelationList() {
        return productCategoryRelationList;
    }

    public void setProductCategoryRelationList(List<SmsCouponProductCategoryRelation> productCategoryRelationList) {
        this.productCategoryRelationList = productCategoryRelationList;
    }
}
