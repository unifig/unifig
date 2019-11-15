package com.unifig.organ.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;

/**
 * <p>
 * 会员积分成长规则表 服务类
 * </p>
 *
 *
 * @since 2019-01-27
 */
public interface UmsMemberIntegrationRuleSettingService extends IService<UmsMemberIntegrationRuleSetting> {

    void cache(String action);

    UmsMemberIntegrationRuleSetting selectByAction(String action);

    void deleteCache(String action);
}
