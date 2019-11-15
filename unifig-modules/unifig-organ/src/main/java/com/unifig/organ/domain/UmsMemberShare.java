package com.unifig.organ.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户收藏的商品
 *    on 2018/8/2.
 */
@Data
@Document(collection = "UmsMemberCollectionProduct")
public class UmsMemberShare {
    @Id
    @ApiModelProperty("数据id")
    private String id;
    /**
     * 用户id  不用传 后台自动从token中获取
     */
    @Indexed
    @ApiModelProperty("用户id")
    private Long memberId;
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
     * 商品id
     */
    @Indexed
    @ApiModelProperty("商品id")
    private Long productId;
    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    private String productName;
    /**
     * 商品图片
     */
    @ApiModelProperty("商品图片")
    private String productPic;
    /**
     * 商品标题
     */
    @ApiModelProperty("商品标题")
    private String productSubTitle;
    /**
     * 商品价格
     */
    @ApiModelProperty("商品价格")
    private String productPrice;
    private Date createTime;
}
