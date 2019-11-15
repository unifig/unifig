package com.unifig.organ.service;

import com.unifig.organ.domain.UmsMemberBrandAttention;

import java.util.List;

/**
 * 会员关注Service
 *    on 2018/8/2.
 */
public interface UmsMemberAttentionService {
    /**
     * 添加关注
     */
    int add(UmsMemberBrandAttention memberBrandAttention);

    /**
     * 取消关注
     */
    int delete(Long memberId, Long brandId);

    /**
     * 获取用户关注列表
     */
    List<UmsMemberBrandAttention> list(Long memberId);
}
