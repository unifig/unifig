package com.unifig.mall.bean.domain;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 搜索中的商品属性信息
 *    on 2018/6/27.
 */
public class EsProductAttributeValue implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "属性id")
    private Long productAttributeId;

    //属性值
    @ApiModelProperty(value = "属性值")
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String value;

    //属性参数：0->规格；1->参数
    @ApiModelProperty(value = "属性参数：0->规格；1->参数")
    private Integer type;

    //属性名称
    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    @ApiModelProperty(value = "属性名称")
    private String name;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductAttributeId() {
        return productAttributeId;
    }

    public void setProductAttributeId(Long productAttributeId) {
        this.productAttributeId = productAttributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
