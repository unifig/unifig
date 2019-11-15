package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticle;
import com.unifig.mall.mapper.CmsArticleMapper;
import com.unifig.mall.service.CmsArticleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.bean.vo.CmsArticleListVo;
import com.unifig.mall.bean.vo.HomeCmsArticleVo;
import com.unifig.utils.BeanMapUtils;
import com.unifig.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Service
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements CmsArticleService {

    @Autowired
    private CmsArticleMapper cmsArticleMapper;
    @Override
    public Page<CmsArticle> selectByCatId(Page<CmsArticle> cmsArticlePage, Integer catId) {
        EntityWrapper<CmsArticle> cmsArticleWrapper = new EntityWrapper<CmsArticle>();
        cmsArticleWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        cmsArticleWrapper.eq("cat_id",catId);
        cmsArticlePage = selectPage(cmsArticlePage,cmsArticleWrapper);
        return cmsArticlePage;
    }

    @Override
    public List<CmsArticleListVo> selectByCatId(Integer page, Integer rows, Integer catId) {
        Page<CmsArticle> cmsArticlePage = selectByCatId(new Page<CmsArticle>(page, rows), catId);
        List<CmsArticle> records = cmsArticlePage.getRecords();
        List<CmsArticleListVo> cmsArticleListVos = BeanMapUtils.copyListJavaBean(records, CmsArticleListVo.class);
        return cmsArticleListVos;
    }

    @Override
    public List<HomeCmsArticleVo> selectHomeList(Map<String, Object> params) {
        List<HomeCmsArticleVo>  cmsArticleHomeVos = cmsArticleMapper.selectHomeList(params);
        return cmsArticleHomeVos;
    }

    @Override
    public int updateNavStatus(List<String> ids, Integer navStatus) {
        CmsArticle cmsArticle = new CmsArticle();
        EntityWrapper<CmsArticle> entityWrapper = new EntityWrapper<CmsArticle>();
        entityWrapper.in("id",ids);
        cmsArticle.setNavStatus(navStatus);
        Integer count = cmsArticleMapper.update(cmsArticle, entityWrapper);
        return count;
    }

    @Override
    public List<HomeCmsArticleVo> selectPageByCatId(Page<CmsArticle> cmsArticlePage, String catId,String title) {
        EntityWrapper<CmsArticle> cmsArticleWrapper = new EntityWrapper<CmsArticle>();
        cmsArticleWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        if(!StringUtils.isNullOrEmpty(catId)){
            cmsArticleWrapper.eq("cat_id",catId);

        }
        if(!StringUtils.isNullOrEmpty(title)){
            cmsArticleWrapper.like("title",title);
        }
        cmsArticlePage =  selectPage(cmsArticlePage, cmsArticleWrapper);

        List<CmsArticle> records = cmsArticlePage.getRecords();
        List<HomeCmsArticleVo> homeCmsArticleVos = BeanMapUtils.copyListJavaBean(records, HomeCmsArticleVo.class);
        return homeCmsArticleVos;
    }
}
