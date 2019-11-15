package com.unifig.mall.service.impl;

import com.unifig.mall.mapper.CmsPrefrenceAreaMapper;
import com.unifig.mall.bean.model.CmsPrefrenceArea;
import com.unifig.mall.bean.model.CmsPrefrenceAreaExample;
import com.unifig.mall.service.CmsPrefrenceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品优选Service实现类
 *    on 2018/6/1.
 */
@Service
public class CmsPrefrenceAreaServiceImpl implements CmsPrefrenceAreaService {
    @Autowired
    private CmsPrefrenceAreaMapper prefrenceAreaMapper;

    @Override
    public List<CmsPrefrenceArea> listAll() {
        return prefrenceAreaMapper.selectByExample(new CmsPrefrenceAreaExample());
    }
}
