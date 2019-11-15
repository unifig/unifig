package com.unifig.mall.util;

import com.unifig.mall.bean.model.CmsArticleCategory;
import com.unifig.mall.bean.vo.CmsArticleCategoryVo;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dozer.DozerBeanMapper;

import java.util.ArrayList;
import java.util.List;

/**

 * @date 2018-10-24
 */
public class BeanUtils extends PropertyUtilsBean {
    private static DozerBeanMapper dozer = new DozerBeanMapper();


    public static List<CmsArticleCategoryVo> CmsArticleCategoryConvertCmsArticleCategoryVo(List<CmsArticleCategory> cmsArticleCategorys) {
        List<CmsArticleCategoryVo> cmsArticleCategoryVos = new ArrayList<CmsArticleCategoryVo>();
        for (CmsArticleCategory cmsArticleCategory : cmsArticleCategorys) {
            cmsArticleCategoryVos.add(new CmsArticleCategoryVo(cmsArticleCategory.getId(), cmsArticleCategory.getCatName()));
        }
        return cmsArticleCategoryVos;

    }

}

