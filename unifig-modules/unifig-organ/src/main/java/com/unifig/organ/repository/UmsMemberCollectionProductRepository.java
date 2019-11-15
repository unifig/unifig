package com.unifig.organ.repository;

import com.unifig.organ.domain.UmsMemberCollectionProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 商品收藏Repository
 *    on 2018/8/2.
 */
public interface UmsMemberCollectionProductRepository extends MongoRepository<UmsMemberCollectionProduct, String> {
    UmsMemberCollectionProduct findByMemberIdAndProductId(Long memberId, Long productId);

    int deleteByMemberIdAndProductId(Long memberId, Long productId);

    List<UmsMemberCollectionProduct> findByMemberId(Long memberId);

    long countByMemberId(Long memberId);
}
