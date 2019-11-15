package com.unifig.mall.service;

import com.unifig.mall.bean.model.LmsOrder;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 物流订单表 服务类
 * </p>
 *
 *
 * @since 2019-02-15
 */
public interface LmsOrderService extends IService<LmsOrder> {

    int selectFreight(String fromAddressId, String toIAddressd);
}
