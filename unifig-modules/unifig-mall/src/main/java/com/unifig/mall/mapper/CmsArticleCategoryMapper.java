package com.unifig.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.mall.bean.model.CmsArticleCategory;
import com.unifig.mall.bean.vo.HomeCmsArticleCategoryVo;

import java.util.List;

/**
 * <p>
 * 文章分类表 Mapper 接口
 * </p>
 *
 *
 * @since 2019-01-22
 */
public interface CmsArticleCategoryMapper extends BaseMapper<CmsArticleCategory> {

    List<HomeCmsArticleCategoryVo> selectHomeList();

}
