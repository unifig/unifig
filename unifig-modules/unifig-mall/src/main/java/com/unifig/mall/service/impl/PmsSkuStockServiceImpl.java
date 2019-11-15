package com.unifig.mall.service.impl;

import com.unifig.mall.service.PmsSkuStockService;
import com.unifig.mall.dao.PmsSkuStockDao;
import com.unifig.mall.mapper.PmsSkuStockMapper;
import com.unifig.mall.bean.model.PmsSkuStock;
import com.unifig.mall.bean.model.PmsSkuStockExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品sku库存管理Service实现类
 *    on 2018/4/27.
 */
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andSkuCodeLike("%" + keyword + "%");
        }
        return skuStockMapper.selectByExample(example);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        return skuStockDao.replaceList(skuStockList);
    }

    @Override
    public PmsSkuStock selectById(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if(keyword != null){
            String[] splits = keyword.split(",");
            if(splits.length ==1){
                criteria.andSp1EqualTo(splits[0]);
            }else if(splits.length ==2){
                criteria.andSp1EqualTo(splits[0]);
                criteria.andSp2EqualTo(splits[1]);
            }else if(splits.length ==3){
                criteria.andSp1EqualTo(splits[0]);
                criteria.andSp2EqualTo(splits[1]);
                criteria.andSp3EqualTo(splits[2]);
            }
        }
        List<PmsSkuStock> pmsSkuStocks = skuStockMapper.selectByExample(example);
        if(pmsSkuStocks != null){
            return pmsSkuStocks.get(0);
        }
        return null;
    }
}
