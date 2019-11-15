package com.unifig.mall.bean.vo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 文章分类表
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Data
@ToString
public class CmsArticleCategoryVo extends Model<CmsArticleCategoryVo> {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 类别名称
     */
    private String catName;


    public CmsArticleCategoryVo() {

    }

    public CmsArticleCategoryVo(String id, String catName) {
        this.id = id;
        this.catName = catName;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
