package com.unifig.mall.bean.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * 文章分类表vo
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Data
@ToString
public class HomeCmsArticleCategoryVo {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty("文章类别id")
    private String id;

    /**
     * 类别名称
     */
    @TableField("cat_name")
    @ApiModelProperty("文章类别名称")
    private String catName;

    /**
     * 分类图片
     */
    @ApiModelProperty("文章类别icone")
    private String pic;

}
