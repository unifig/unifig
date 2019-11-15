package com.unifig.organ.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.model.UmsIntegrationChangeHistory;

/**
 * <p>
 * 积分变化历史记录表 服务类
 * </p>
 *
 *
 * @since 2019-01-28
 */
public interface UmsIntegrationChangeHistoryService extends IService<UmsIntegrationChangeHistory> {

    int selectUse(String userId);
}
