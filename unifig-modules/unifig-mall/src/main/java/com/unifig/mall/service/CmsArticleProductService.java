package com.unifig.mall.service;

import com.unifig.mall.bean.model.CmsArticleProduct;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 文章商品关联表 服务类
 * </p>
 *
 *
 * @since 2019-02-18
 */
public interface CmsArticleProductService extends IService<CmsArticleProduct> {

    List<CmsArticleProduct> selectListByArticleId(String articleId);
}
