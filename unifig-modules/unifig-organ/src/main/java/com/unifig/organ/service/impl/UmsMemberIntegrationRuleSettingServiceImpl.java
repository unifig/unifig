package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.context.Constants;
import com.unifig.context.RedisKeyGenerate;
import com.unifig.organ.mapper.UmsMemberIntegrationRuleSettingMapper;
import com.unifig.organ.model.UmsMemberIntegrationRuleSetting;
import com.unifig.organ.service.UmsMemberIntegrationRuleSettingService;
import com.unifig.utils.CacheRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员积分成长规则表 服务实现类
 * </p>
 *
 *
 * @since 2019-01-27
 */
@Service
public class UmsMemberIntegrationRuleSettingServiceImpl extends ServiceImpl<UmsMemberIntegrationRuleSettingMapper, UmsMemberIntegrationRuleSetting> implements UmsMemberIntegrationRuleSettingService {

    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    @Override
    public void cache(String action) {
        if (action == null) {
            //刷新整个缓存
            List<UmsMemberIntegrationRuleSetting> umsMemberIntegrationRuleSettings = selectList(new EntityWrapper<UmsMemberIntegrationRuleSetting>());
            for (UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting : umsMemberIntegrationRuleSettings) {
                cacheRedisUtils.hset(RedisKeyGenerate.memeberRuleSetting(), umsMemberIntegrationRuleSetting.getAction(), umsMemberIntegrationRuleSetting);
            }
        } else {
            //根新其中一个action
            UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = selectByAction(action);
            cacheRedisUtils.hset(RedisKeyGenerate.memeberRuleSetting(), umsMemberIntegrationRuleSetting.getAction(), umsMemberIntegrationRuleSetting);

        }
    }
    @Override
    public UmsMemberIntegrationRuleSetting selectByAction(String action) {
        EntityWrapper<UmsMemberIntegrationRuleSetting> umsMemberIntegrationRuleSettingEntityWrapper = new EntityWrapper<UmsMemberIntegrationRuleSetting>();
        umsMemberIntegrationRuleSettingEntityWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
        umsMemberIntegrationRuleSettingEntityWrapper.eq("action", action);
        UmsMemberIntegrationRuleSetting umsMemberIntegrationRuleSetting = selectOne(umsMemberIntegrationRuleSettingEntityWrapper);
        return umsMemberIntegrationRuleSetting;
    }

    @Override
    public void deleteCache(String action) {
        cacheRedisUtils.hdel(RedisKeyGenerate.memeberRuleSetting(),action);
    }
}
