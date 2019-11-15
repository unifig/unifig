package com.unifig.organ.service.impl;

import com.unifig.organ.domain.UmsMemberReadHistory;
import com.unifig.organ.repository.UmsMemberReadHistoryRepository;
import com.unifig.organ.service.UmsMemberReadHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员浏览记录管理Service实现类
 *    on 2018/8/3.
 */
@Service
public class UmsMemberReadHistoryServiceImpl implements UmsMemberReadHistoryService {
    @Autowired
    private UmsMemberReadHistoryRepository memberReadHistoryRepository;

    @Override
    public int create(UmsMemberReadHistory memberReadHistory) {
        memberReadHistory.setId(null);
        memberReadHistory.setCreateTime(new Date());
        memberReadHistoryRepository.save(memberReadHistory);
        return 1;
    }

    @Override
    public int delete(List<String> ids) {
        List<UmsMemberReadHistory> deleteList = new ArrayList<>();
        for (String id : ids) {
            UmsMemberReadHistory memberReadHistory = new UmsMemberReadHistory();
            memberReadHistory.setId(id);
            deleteList.add(memberReadHistory);
        }
        memberReadHistoryRepository.delete(deleteList);
        return ids.size();
    }

    @Override
    public List<UmsMemberReadHistory> list(Long memberId) {
        return memberReadHistoryRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
    }
}
