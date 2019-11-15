package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.context.Constants;
import com.unifig.organ.mapper.UmsIntegrationChangeHistoryMapper;
import com.unifig.organ.model.UmsIntegrationChangeHistory;
import com.unifig.organ.service.UmsIntegrationChangeHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 积分变化历史记录表 服务实现类
 * </p>
 *
 *
 * @since 2019-01-28
 */
@Service
public class UmsIntegrationChangeHistoryServiceImpl extends ServiceImpl<UmsIntegrationChangeHistoryMapper, UmsIntegrationChangeHistory> implements UmsIntegrationChangeHistoryService {

    @Override
    public int selectUse(String userId) {
        EntityWrapper<UmsIntegrationChangeHistory> umsIntegrationChangeHistoryWrapper = new EntityWrapper<UmsIntegrationChangeHistory>();
        umsIntegrationChangeHistoryWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        umsIntegrationChangeHistoryWrapper.eq("change_type", Constants.DEFAULT_VAULE_ZERO);
        umsIntegrationChangeHistoryWrapper.eq("member_id", userId);
        List<UmsIntegrationChangeHistory> umsIntegrationChangeHistories = selectList(umsIntegrationChangeHistoryWrapper);
        int use = 0;
        for (UmsIntegrationChangeHistory umsIntegrationChangeHistory : umsIntegrationChangeHistories) {
            use=use-umsIntegrationChangeHistory.getChangeCount();
        }
        return use;
    }

    public static void main(String[] args) {
        int a=0-Integer.parseInt(-1+"");
        System.out.println(a);
    }
}
