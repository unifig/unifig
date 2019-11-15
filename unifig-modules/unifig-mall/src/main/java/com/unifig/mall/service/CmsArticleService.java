package com.unifig.mall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.mall.bean.model.CmsArticle;
import com.unifig.mall.bean.vo.CmsArticleListVo;
import com.unifig.mall.bean.vo.HomeCmsArticleVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 *
 * @since 2019-01-22
 */
public interface CmsArticleService extends IService<CmsArticle> {

    Page<CmsArticle> selectByCatId(Page<CmsArticle> cmsArticlePage, Integer catId);

    List<CmsArticleListVo> selectByCatId(Integer page, Integer rows, Integer catId);

    List<HomeCmsArticleVo> selectHomeList(Map<String, Object> params);

    int updateNavStatus(List<String> ids, Integer navStatus);

    List<HomeCmsArticleVo> selectPageByCatId(Page<CmsArticle> homeCmsArticleVoPage, String catId,String title);
}
