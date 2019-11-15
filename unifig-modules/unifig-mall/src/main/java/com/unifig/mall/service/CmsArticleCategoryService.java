package com.unifig.mall.service;

import com.unifig.mall.bean.model.CmsArticleCategory;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.mall.bean.vo.CmsArticleCategoryVo;
import com.unifig.mall.bean.vo.HomeCmsArticleCategoryVo;

import java.util.List;

/**
 * <p>
 * 文章分类表 服务类
 * </p>
 *
 *
 * @since 2019-01-22
 */
public interface CmsArticleCategoryService extends IService<CmsArticleCategory> {

    List<HomeCmsArticleCategoryVo> selectHomeList();

    int updateNavStatus(List<String> ids, Integer navStatus);

    int updateShowStatus(List<String> ids, Integer showStatus);

    List<CmsArticleCategoryVo> selectList(String catName);
}
