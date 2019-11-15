package com.unifig.mall.bean.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
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
@TableName("cms_article_category")
public class CmsArticleCategory extends Model<CmsArticleCategory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    /**
     * 类别名称
     */
    @TableField("cat_name")
    private String catName;
    /**
     * 默认分组
     */
    @TableField("cat_type")
    private Integer catType;
    /**
     * 夫级ID
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;
    /**
     * 分类描述
     */
    @TableField("cat_desc")
    private String catDesc;
    /**
     * 搜索关键词
     */
    private String keywords;
    /**
     * 别名
     */
    @TableField("cat_alias")
    private String catAlias;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
    /**
     * 分类图片
     */
    private String pic;
    /**
     * 0=删除 1=可用
     */
    private Integer enable;

    /**
     * 是否显示在导航栏：0->不显示；1->显示
     *
     * @mbggenerated
     */
    private Integer navStatus;

    /**
     * 显示状态：0->不显示；1->显示
     *
     * @mbggenerated
     */
    private Integer showStatus;



    @Override
    protected Serializable pkVal() {
        return null;
    }

}
