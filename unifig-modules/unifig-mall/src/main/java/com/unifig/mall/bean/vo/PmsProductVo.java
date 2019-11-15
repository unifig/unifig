package com.unifig.mall.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unifig.mall.bean.domain.EsProductAttributeValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品视图
 */
@Data
public class PmsProductVo {
    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id")
    private Long id;
    /**
     *
     */
    @ApiModelProperty(value = "商品编号")
    private String productSn;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    /**
     * 商品类型id
     */
    @ApiModelProperty(value = "商品分类id")
    private Long productCategoryId;

    @ApiModelProperty(value = "商品分类名称")
    private String productCategoryName;
    /**
     * 图片
     */
    @ApiModelProperty(value = "图片")
    private String pic;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品标题")
    private String subTitle;

    @ApiModelProperty(value = "关键字")
    private String keywords;
    /**
     * 商品价格
     */
    @ApiModelProperty(value = "商品价格")
    private BigDecimal price;
    /**
     * 销量
     */
    @ApiModelProperty(value = "销量")
    private Integer sale;

    @ApiModelProperty(value = "新品状态:0->不是新品；1->新品")
    private Integer newStatus;

    @ApiModelProperty(value = "推荐状态；0->不推荐；1->推荐")
    private Integer recommandStatus;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购")
    private Integer promotionType;

    @ApiModelProperty(value = "排序")
    private Integer sort;
    /**
     * 商品属性
     */
    @ApiModelProperty(value = "商品属性")
    private List<EsProductAttributeValue> attrValueList;
    /**
     * 类型 1 普通商品 2 积分商品

     */
    @ApiModelProperty(value = "类型 1 普通商品 2 积分商品")
    private Integer type;
    /**
     * 积分
     */
    @ApiModelProperty(value = "积分")
    private Integer usePointLimit;

    /**
     * 市场价
     */
    @ApiModelProperty(value = "市场价")
    private BigDecimal originalPrice;

    /**
     * 商品图册
     */
    @ApiModelProperty(value = "商品图册")
    private String albumPics;

    /**
     * 移动端详情页
     */
    @ApiModelProperty(value = "移动端详情页---html")
    private String detailMobileHtml;

    /**
     * 是否团购
     */
    @ApiModelProperty(value = "是否团购")
    private Integer group;

    @ApiModelProperty(value = "团购价格")
    private BigDecimal groupPurchasePrice;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private String shopId;

    @ApiModelProperty(value = "产品服务(逗号隔开) 1->无忧退货；2->快速退款；3->免费包邮")
    private String serviceIds;


    @ApiModelProperty("是否收藏")
    private boolean collect;

    /**
     * 团购规则
     */
    @ApiModelProperty("团购规则")
    private String regulation;


}
