package com.unifig.mall.bean.domain;

import com.unifig.mall.bean.model.PmsProduct;
import com.unifig.mall.bean.model.PmsProductAttribute;
import com.unifig.mall.bean.model.PmsSkuStock;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 购物车中选择规格的商品信息
 *    on 2018/8/2.
 */
public class CartProduct extends PmsProduct {
    @ApiModelProperty("商品属性集合")
    private List<PmsProductAttribute> productAttributeList;
    @ApiModelProperty("sku信息集合")
    private List<PmsSkuStock> skuStockList;

    public List<PmsProductAttribute> getProductAttributeList() {
        return productAttributeList;
    }

    public void setProductAttributeList(List<PmsProductAttribute> productAttributeList) {
        this.productAttributeList = productAttributeList;
    }

    public List<PmsSkuStock> getSkuStockList() {
        return skuStockList;
    }

    public void setSkuStockList(List<PmsSkuStock> skuStockList) {
        this.skuStockList = skuStockList;
    }
}
