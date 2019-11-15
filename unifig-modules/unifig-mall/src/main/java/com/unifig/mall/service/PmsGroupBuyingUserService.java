package com.unifig.mall.service;

import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.result.ResultData;

/**
 * <p>
 * 用户参团记录 服务类
 * </p>
 *
 *
 * @since 2019-06-25
 */
public interface PmsGroupBuyingUserService extends IService<PmsGroupBuyingUser> {

    Integer USER_TYPE_INITIATE = 1;

    Integer USER_TYPE_JOIN = 2;

    /**
     * 添加用户参团记录
     * @param parentId  团购id
     * @param type  类型
     * @return
     */
    boolean record(String parentId,Integer type,String orderId);

    ResultData list(Integer pageSize, Integer pageNum, Integer type, String userId);
}
