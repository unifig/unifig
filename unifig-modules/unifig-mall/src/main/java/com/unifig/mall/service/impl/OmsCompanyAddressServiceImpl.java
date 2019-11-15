package com.unifig.mall.service.impl;

import com.unifig.mall.bean.model.OmsCompanyAddressExample;
import com.unifig.mall.mapper.OmsCompanyAddressMapper;
import com.unifig.mall.bean.model.OmsCompanyAddress;
import com.unifig.mall.service.OmsCompanyAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址管理Service实现类
 *    on 2018/10/18.
 */
@Service
public class OmsCompanyAddressServiceImpl implements OmsCompanyAddressService {
    @Autowired
    private OmsCompanyAddressMapper companyAddressMapper;
    @Override
    public List<OmsCompanyAddress> list() {
        return companyAddressMapper.selectByExample(new OmsCompanyAddressExample());
    }
}
