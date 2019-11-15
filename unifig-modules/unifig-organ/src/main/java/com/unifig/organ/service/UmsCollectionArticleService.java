package com.unifig.organ.service;

import com.unifig.organ.domain.UmsMemberCollectionArticle;

import java.util.List;

/**
 * 会员收藏Service
 *    on 2018/8/2.
 */
public interface UmsCollectionArticleService {
    int addArticle(UmsMemberCollectionArticle ArticleCollection);

    int deleteArticle(String memberId, String ArticleId);

    List<UmsMemberCollectionArticle> listArticle(String memberId);

    List<UmsMemberCollectionArticle> listArticleIndex(String memberId);

    long countArticle(String memberId);
}
