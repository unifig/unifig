package com.unifig.organ.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户收藏的文章
 *    on 2018/8/2.
 */
@Data
@Document(collection = "UmsMemberCollectionArticle")
public class UmsMemberCollectionArticle {
    @Id
    @ApiModelProperty("数据id")
    private String id;
    /**
     * 用户id  不用传 后台自动从token中获取
     */
    @Indexed
    @ApiModelProperty("用户id")
    private String memberId;
    /**
     * 用户昵称
     */
    @ApiModelProperty("用户名称")
    private String nickname;
    /**
     * 用户头像
     */
    @ApiModelProperty("头像")
    private String avatar;
    /**
     * 文章id
     */
    @Indexed
    @ApiModelProperty("文章id")
    private String articleId;
    /**
     * 文章标题
     */
    @ApiModelProperty("文章标题")
    private String title;
    /**
     * 文章摘要
     */
    @ApiModelProperty("文章摘要")
    private String description;
    /**
     * 文章缩略图
     */
    @ApiModelProperty("文章缩略图")
    private String thumb;

    private Date createTime;
}
