package com.unifig.bi.analysis.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文章分类表
 * </p>
 *
 *
 * @since 2019-03-21
 */
@Data
@TableName("st_cms_article")
public class StCmsArticle extends Model<StCmsArticle> {

    private static final long serialVersionUID = 1L;

    private String id;
    @TableField("dept_id")
    private String deptId;
    @TableField("dept_name")
    private String deptName;
    @TableField("user_id")
    private String userId;
    @TableField("user_name")
    private String userName;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
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
     * 0=删除 1=可用
     */
    private Integer enable;

    /**
     * 统计时间
     */
    @TableField("statis_date")
    private Integer statisDate;


    /**
     * 统计时间
     */
    @TableField("article_id")
    private String articleId;


    /**
     * 统计时间 小时
     */
    private Integer hour;

    /**
     * 次数
     */
    @TableField("read_count")
    private Integer readCount;




    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
