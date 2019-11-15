package com.unifig.mall.service.impl;

import com.unifig.mall.bean.model.LmsOrder;
import com.unifig.mall.mapper.LmsOrderMapper;
import com.unifig.mall.service.LmsOrderService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 物流订单表 服务实现类
 * </p>
 *
 *
 * @since 2019-02-15
 */
@Service
public class LmsOrderServiceImpl extends ServiceImpl<LmsOrderMapper, LmsOrder> implements LmsOrderService {

    @Override
    public int selectFreight(String fromAddressId, String toIAddressd) {
        return 0;
    }
}
