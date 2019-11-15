package com.unifig.organ.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 搜索中的店铺信息
 * Created by   on 2019/03/12.
 */
@Document(indexName = "client", type = "shop",shards = 1,replicas = 0)
@Data
public class EsShop implements Serializable {
    private static final long serialVersionUID = -1L;


    /**
     * 主键
     */
    @ApiModelProperty(value = "主键ID由32位UUID生成", name = "id")
    @Id
    private String id;
    /**
     * 平台id
     */
    @ApiModelProperty("平台id")
    private String terraceId;
    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String name;
    /**
     * logo
     */
    @ApiModelProperty("logo")
    private String logo;
    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;
    /**
     * 图片
     */
    @ApiModelProperty("图片")
    private String picture;
    /**
     * 地址
     */
    @ApiModelProperty("地址")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String site;
    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private BigDecimal longitude;
    /**
     * 维度
     */
    @ApiModelProperty("维度")
    private BigDecimal latitude;
    /**
     * 简介
     */
    @ApiModelProperty("简介")
    @Field(analyzer = "ik_max_word",type = FieldType.String)
    private String intro;
    /**
     * 营业时间
     */
    @ApiModelProperty("营业时间")
    private String businessHours;
    /**
     * 状态 0  正常   1 关闭
     */
    @ApiModelProperty("状态 0  正常   1 关闭")
    private Integer status;
    /**
     * 开店时间
     */
    @ApiModelProperty("开店时间")
    private Date createTime;
    /**
     * 省
     */
    @ApiModelProperty("省")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty("市")
    private String city;
    /**
     * 县(区)
     */
    @ApiModelProperty("县(区)")
    private String area;
    /**
     * 类型 0 普通店铺 1 自营店铺
     */
    @ApiModelProperty("类型 0 普通店铺 1 自营店铺")
    private Integer type;

    /** 经维度，中间逗号隔开 */
    @ApiModelProperty("经维度，中间逗号隔开")
    @GeoPointField
    private String location;

    @ApiModelProperty("直线距离 单位 M")
    private double distance;
}
