package com.unifig.mall.bean.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PmsProductCategoryVo implements Serializable {
    private Long id;

    /**
     * 上机分类的编号：0表示一级分类
     *
     * @mbggenerated
     */
    private Long parentId;

    private String name;


    /**
     * 图标
     *
     * @mbggenerated
     */
    private String icon;


    private static final long serialVersionUID = 1L;


}