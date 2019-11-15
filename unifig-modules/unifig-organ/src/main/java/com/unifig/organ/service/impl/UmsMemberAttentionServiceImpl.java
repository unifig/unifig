package com.unifig.organ.service.impl;

import com.unifig.organ.domain.UmsMemberBrandAttention;
import com.unifig.organ.repository.UmsMemberBrandAttentionRepository;
import com.unifig.organ.service.UmsMemberAttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员关注Service实现类
 *    on 2018/8/2.
 */
@Service
public class UmsMemberAttentionServiceImpl implements UmsMemberAttentionService {
    @Autowired
    private UmsMemberBrandAttentionRepository memberBrandAttentionRepository;

    @Override
    public int add(UmsMemberBrandAttention memberBrandAttention) {
        int count = 0;
        UmsMemberBrandAttention findAttention = memberBrandAttentionRepository.findByMemberIdAndBrandId(memberBrandAttention.getMemberId(), memberBrandAttention.getBrandId());
        if (findAttention == null) {
            memberBrandAttentionRepository.save(memberBrandAttention);
            count = 1;
        }
        return count;
    }

    @Override
    public int delete(Long memberId, Long brandId) {
        return memberBrandAttentionRepository.deleteByMemberIdAndBrandId(memberId, brandId);
    }

    @Override
    public List<UmsMemberBrandAttention> list(Long memberId) {
        return memberBrandAttentionRepository.findByMemberId(memberId);
    }
}
