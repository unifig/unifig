package com.unifig.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.mall.bean.model.OmsOrderAfterSale;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.result.ResultData;

/**
 * <p>
 *
 *
 * @since 2019-01-23
 */
public interface OmsOrderAfterSaleService extends IService<OmsOrderAfterSale> {

    ResultData create(OmsOrderAfterSale returnApply);

    ResultData list(Integer page, Integer rows, String createTime, String updateTime, Integer status);

    ResultData answer(String id);

    ResultData refund(String id, String money, String type);
}
