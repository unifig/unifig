package com.unifig.mall.bean.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * 商品分类表vo
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Data
@ToString
public class HomePmsProductCategoryVo {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 类别名称
     */
    private String catName;

    /**
     * 分类图片
     */
    private String pic;

}
