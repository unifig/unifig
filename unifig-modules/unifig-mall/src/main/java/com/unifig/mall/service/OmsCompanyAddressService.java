package com.unifig.mall.service;

import com.unifig.mall.bean.model.OmsCompanyAddress;

import java.util.List;

/**
 * 收货地址管Service
 *    on 2018/10/18.
 */
public interface OmsCompanyAddressService {
    /**
     * 获取全部收货地址
     */
    List<OmsCompanyAddress> list();
}
