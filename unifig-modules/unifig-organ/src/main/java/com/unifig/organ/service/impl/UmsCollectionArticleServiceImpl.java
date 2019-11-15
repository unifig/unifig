package com.unifig.organ.service.impl;

import com.unifig.organ.domain.UmsMemberCollectionArticle;
import com.unifig.organ.repository.UmsMemberCollectionArticleRepository;
import com.unifig.organ.service.UmsCollectionArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员收藏Service实现类
 *    on 2018/8/2.
 */
@Service
public class UmsCollectionArticleServiceImpl implements UmsCollectionArticleService {
    @Autowired
    private UmsMemberCollectionArticleRepository ArticleCollectionRepository;

    @Override
    public int addArticle(UmsMemberCollectionArticle ArticleCollection) {
        int count = 0;
        UmsMemberCollectionArticle findCollection = ArticleCollectionRepository.findByMemberIdAndArticleId(ArticleCollection.getMemberId(), ArticleCollection.getArticleId());
        if (findCollection == null) {
            ArticleCollectionRepository.save(ArticleCollection);
            count = 1;
        }
        return count;
    }

    @Override
    public int deleteArticle(String memberId, String ArticleId) {
        return ArticleCollectionRepository.deleteByMemberIdAndArticleId(memberId, ArticleId);
    }

    @Override
    public List<UmsMemberCollectionArticle> listArticle(String memberId) {
        return ArticleCollectionRepository.findByMemberId(memberId);
    }

    @Override
    public List<UmsMemberCollectionArticle> listArticleIndex(String memberId) {
        List<UmsMemberCollectionArticle> umsMemberArticleCollections = ArticleCollectionRepository.findByMemberId(memberId);
        if (umsMemberArticleCollections.size() > 3) {
            return umsMemberArticleCollections.subList(0, 2);
        }
        return umsMemberArticleCollections;
    }

    @Override
    public long countArticle(String memberId) {
        long count = ArticleCollectionRepository.countByMemberId(memberId);
        return count;
    }
}
