package com.unifig.organ.service;

import com.unifig.organ.domain.UmsMemberReadHistory;

import java.util.List;

/**
 * 会员浏览记录管理Service
 *    on 2018/8/3.
 */
public interface UmsMemberReadHistoryService {
    /**
     * 生成浏览记录
     */
    int create(UmsMemberReadHistory memberReadHistory);

    /**
     * 批量删除浏览记录
     */
    int delete(List<String> ids);

    /**
     * 获取用户浏览历史记录
     */
    List<UmsMemberReadHistory> list(Long memberId);
}
