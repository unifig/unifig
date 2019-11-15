package com.unifig.bi.analysis.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.bi.analysis.model.StCmsArticle;

import java.sql.Date;
import java.util.Map;

/**
 * <p>
 * 文章分类表 服务类
 * </p>
 *
 *
 * @since 2019-03-21
 */
public interface StCmsArticleService extends IService<StCmsArticle> {

    Map<String, Object> line(Date startDate, Date stopDate, String articleId);
}
