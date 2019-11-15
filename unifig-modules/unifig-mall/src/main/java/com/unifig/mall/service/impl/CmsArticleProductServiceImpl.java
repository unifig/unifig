package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsArticleProduct;
import com.unifig.mall.mapper.CmsArticleProductMapper;
import com.unifig.mall.service.CmsArticleProductService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文章商品关联表 服务实现类
 * </p>
 *
 *
 * @since 2019-02-18
 */
@Service
public class CmsArticleProductServiceImpl extends ServiceImpl<CmsArticleProductMapper, CmsArticleProduct> implements CmsArticleProductService {

    @Override
    public List<CmsArticleProduct> selectListByArticleId(String articleId) {
        EntityWrapper<CmsArticleProduct> cmsArticleWrapper = new EntityWrapper<CmsArticleProduct>();
        cmsArticleWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        cmsArticleWrapper.eq("article_id", articleId);
        List<CmsArticleProduct> cmsArticleProducts = selectList(cmsArticleWrapper);
        return cmsArticleProducts;
    }
}
