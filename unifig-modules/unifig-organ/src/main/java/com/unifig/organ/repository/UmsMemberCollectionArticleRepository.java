package com.unifig.organ.repository;

import com.unifig.organ.domain.UmsMemberCollectionArticle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 商品收藏Repository
 *    on 2018/8/2.
 */
public interface UmsMemberCollectionArticleRepository extends MongoRepository<UmsMemberCollectionArticle, String> {
    UmsMemberCollectionArticle findByMemberIdAndArticleId(String memberId, String articleId);

    int deleteByMemberIdAndArticleId(String memberId, String articleId);

    List<UmsMemberCollectionArticle> findByMemberId(String memberId);

    long countByMemberId(String memberId);
}
