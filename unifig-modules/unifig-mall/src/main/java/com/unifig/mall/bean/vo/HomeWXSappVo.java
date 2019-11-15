package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class HomeWXSappVo implements Serializable {

    @ApiModelProperty("文章分类")
    private List<HomeCmsArticleCategoryVo> homeCmsArticleCategoryVos;

    @ApiModelProperty("文章")
    private List<HomeCmsArticleVo> cmsArticleHomeVos;

    @ApiModelProperty("轮播图列表")
    private List<HomeSmsAdvertiseVo> homeSmsAdvertiseVos;


}