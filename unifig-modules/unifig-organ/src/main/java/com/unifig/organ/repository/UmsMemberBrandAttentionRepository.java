package com.unifig.organ.repository;

import com.unifig.organ.domain.UmsMemberBrandAttention;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 会员关注Repository
 *    on 2018/8/2.
 */
public interface UmsMemberBrandAttentionRepository extends MongoRepository<UmsMemberBrandAttention, String> {
    UmsMemberBrandAttention findByMemberIdAndBrandId(Long memberId, Long brandId);

    int deleteByMemberIdAndBrandId(Long memberId, Long brandId);

    List<UmsMemberBrandAttention> findByMemberId(Long memberId);
}
