package com.unifig.mall.service;

import com.unifig.mall.bean.domain.OmsOrderReturnApplyParam;

/**
 * 订单退货管理Service
 *    on 2018/10/17.
 */
public interface OmsPortalOrderReturnApplyService {
    /**
     * 提交申请
     */
    int create(OmsOrderReturnApplyParam returnApply);
}
