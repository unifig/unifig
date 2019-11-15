package com.unifig.mall.service;

import com.unifig.mall.bean.model.PmsGroupBuying;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 商品团购表 服务类
 * </p>
 *
 *
 * @since 2019-01-23
 */
public interface PmsGroupBuyingService extends IService<PmsGroupBuying> {

    PmsGroupBuying create(PmsGroupBuying pmsGroupBuying);

    PmsGroupBuying update(PmsGroupBuying pmsGroupBuying);

}
