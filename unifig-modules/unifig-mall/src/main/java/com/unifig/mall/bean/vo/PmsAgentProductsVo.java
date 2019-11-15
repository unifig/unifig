package com.unifig.mall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 代理商品视图
 */
@Data
public class PmsAgentProductsVo {

    private String id;
    /**
     * 图片
     */
    private String pic;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 积分
     */
    private Integer integral;
    /**
     * 代理人数
     */
    private Integer agencyNumber;
    /**
     *是否以代理
     */
    private Boolean choice =false;

    /**
     * 分享次数
     */
    private Integer shareNumber;
}
