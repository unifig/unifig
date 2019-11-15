package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticleCategory;
import com.unifig.mall.bean.vo.CmsArticleCategoryVo;
import com.unifig.mall.mapper.CmsArticleCategoryMapper;
import com.unifig.mall.service.CmsArticleCategoryService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.bean.vo.HomeCmsArticleCategoryVo;
import com.unifig.mall.util.BeanUtils;
import com.unifig.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文章分类表 服务实现类
 * </p>
 *
 *
 * @since 2019-01-22
 */
@Service
public class CmsArticleCategoryServiceImpl extends ServiceImpl<CmsArticleCategoryMapper, CmsArticleCategory> implements CmsArticleCategoryService {

    @Autowired
    private CmsArticleCategoryMapper cmsArticleCategoryMapper;

    @Override
    public List<HomeCmsArticleCategoryVo> selectHomeList() {
        List<HomeCmsArticleCategoryVo>  homeCmsArticleCategoryVos = cmsArticleCategoryMapper.selectHomeList();
        return homeCmsArticleCategoryVos;
    }

    @Override
    public int updateNavStatus(List<String> ids, Integer navStatus) {
        CmsArticleCategory cmsArticleCategory = new CmsArticleCategory();
        EntityWrapper<CmsArticleCategory> entityWrapper = new EntityWrapper<CmsArticleCategory>();
        entityWrapper.in("id",ids);
        cmsArticleCategory.setNavStatus(navStatus);
        Integer count = cmsArticleCategoryMapper.update(cmsArticleCategory, entityWrapper);
        return count;
    }

    @Override
    public int updateShowStatus(List<String> ids, Integer showStatus) {
        CmsArticleCategory cmsArticleCategory = new CmsArticleCategory();
        EntityWrapper<CmsArticleCategory> entityWrapper = new EntityWrapper<CmsArticleCategory>();
        entityWrapper.in("id",ids);
        cmsArticleCategory.setShowStatus(showStatus);
        Integer count = cmsArticleCategoryMapper.update(cmsArticleCategory, entityWrapper);
        return count;
    }

    @Override
    public List<CmsArticleCategoryVo> selectList(String catName) {
        EntityWrapper<CmsArticleCategory> entityWrapper = new EntityWrapper<CmsArticleCategory>();
        entityWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        if(StringUtils.isNotEmpty(catName)){
            entityWrapper.eq("cat_name",catName);
        }
        List<CmsArticleCategory> cmsArticleCategories = selectList(entityWrapper);
        List<CmsArticleCategoryVo> cmsArticleCategoryVos = BeanUtils.CmsArticleCategoryConvertCmsArticleCategoryVo(cmsArticleCategories);
        return cmsArticleCategoryVos;
    }
}
