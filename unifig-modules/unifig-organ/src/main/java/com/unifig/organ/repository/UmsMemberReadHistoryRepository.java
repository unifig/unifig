package com.unifig.organ.repository;

import com.unifig.organ.domain.UmsMemberReadHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * 会员商品浏览历史Repository
 *    on 2018/8/3.
 */
public interface UmsMemberReadHistoryRepository extends MongoRepository<UmsMemberReadHistory, String> {
    List<UmsMemberReadHistory> findByMemberIdOrderByCreateTimeDesc(Long memberId);
}
