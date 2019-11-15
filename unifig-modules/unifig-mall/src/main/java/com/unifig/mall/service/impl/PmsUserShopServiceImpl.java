package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.context.Constants;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.domain.EsProduct;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.bean.model.PmsUserShop;
import com.unifig.mall.mapper.PmsUserShopMapper;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsUserShopService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.bean.vo.PmsAgentProductsVo;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 我的店铺列表 服务实现类
 * </p>
 *
 *
 * @since 2019-02-19
 */
@Service
public class PmsUserShopServiceImpl extends ServiceImpl<PmsUserShopMapper, PmsUserShop> implements PmsUserShopService {

    @Autowired
    private PmsUserShopMapper pmsUserShopMapper;

    @Autowired
    private EsProductService esProductService;

    @Override
    public PmsUserShop create(PmsUserShop pmsUserShop) {
        pmsUserShop.setCreateTime(new Date());
        pmsUserShopMapper.insert(pmsUserShop);
        return pmsUserShop;
    }

    @Override
    public PmsUserShop remove(PmsUserShop pmsUserShop) {
        pmsUserShop.setStatus(1);
        pmsUserShopMapper.updateById(pmsUserShop);
        return pmsUserShop;
    }

    @Override
    public ResultData selectProductList(Integer page, Integer rows,UserCache userCache) {
        EntityWrapper<PmsUserShop> wrapper = new EntityWrapper<PmsUserShop>();
        wrapper.eq("status", Constants.DEFAULT_VAULE_ZERO);
        List<PmsUserShop> pmsUserShops = pmsUserShopMapper.selectList(wrapper);
        Map<String,List<PmsUserShop>> pmsUserShopsMap = pmsUserShops.stream().collect(Collectors.groupingBy(PmsUserShop::getProductId));
        Page<EsProduct> esProductPage = esProductService.search(page, rows);
        List<PmsAgentProductsVo> productsVos = new ArrayList<>();
        esProductPage.getContent().forEach(li ->{
            PmsAgentProductsVo vo = new PmsAgentProductsVo();
            vo.setId(li.getId().toString());
            vo.setPic(li.getPic());
            vo.setName(li.getName());
            vo.setPrice(li.getPrice());
            vo.setIntegral(100);
            if(pmsUserShopsMap.get(li.getId())!=null){
                vo.setAgencyNumber(pmsUserShopsMap.get(li.getId())!=null ? pmsUserShopsMap.get(li.getId()).size() :0);
                List<PmsUserShop> collect = pmsUserShopsMap.get(li.getId()).stream().filter(ls -> ls.getUserId().equals(userCache.getUserId())).collect(Collectors.toList());
                vo.setChoice(collect.size()>0 ? true:false);
            }else{
                vo.setAgencyNumber(0);
            }

            productsVos.add(vo);
        });
        return ResultData.result(true).setData(productsVos).setCount(esProductPage.getSize());
    }

    @Override
    public ResultData selectMyProductList(Integer page, Integer rows, UserCache userCache) {
        EntityWrapper<PmsUserShop> wrapper = new EntityWrapper<PmsUserShop>();
        wrapper.eq("status", Constants.DEFAULT_VAULE_ZERO);
        wrapper.eq("user_id",userCache.getUserId());
        List<PmsUserShop> pmsUserShops = pmsUserShopMapper.selectPage(new com.baomidou.mybatisplus.plugins.Page<PmsGroupBuying>(page, rows), wrapper);
        List<PmsAgentProductsVo> productsVos = new ArrayList<>();
        pmsUserShops.forEach((PmsUserShop li) ->{
            PmsAgentProductsVo vo = new PmsAgentProductsVo();
            List<EsProduct> content = esProductService.search(Long.valueOf(li.getProductId())).getContent();
            if(content != null && content.size()>0){
                EsProduct esProduct = content.get(0);
                vo.setId(esProduct.getId().toString());
                vo.setPic(esProduct.getPic());
                vo.setName(esProduct.getName());
                vo.setPrice(esProduct.getPrice());
                vo.setIntegral(100);
                vo.setChoice(true);
                vo.setShareNumber(li.getShareNumber());
                productsVos.add(vo);
            }
        });
        return ResultData.result(true).setData(productsVos).setCount(pmsUserShopMapper.selectCount(wrapper));
    }
}
