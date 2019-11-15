package com.unifig.mall.service.impl;

import com.unifig.mall.bean.vo.PmsProductVo;
import com.unifig.mall.mapper.PmsProductMapper;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.mapper.PmsGroupBuyingMapper;
import com.unifig.mall.bean.model.PmsProduct;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsGroupBuyingService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * Product group purchase table service implementation class
 * </p>
 *
 *
 * @since 2019-01-23
 */
@Service
public class PmsGroupBuyingServiceImpl extends ServiceImpl<PmsGroupBuyingMapper, PmsGroupBuying> implements PmsGroupBuyingService {

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private EsProductService esProductService;

    @Autowired
    private PmsGroupBuyingMapper pmsGroupBuyingMapper;


    //是
    public static final Integer CONSTANT_GROUP_TRUE = 1;
    //否
    public static final Integer CONSTANT_GROUP_FALSE = 0;
    //Open group purchase
    public static final Integer CONSTANT_GROUP_STATUS_OPEN = 1;
    //End group purchase
    public static final Integer CONSTANT_GROUP_STATUS_END = 2;

    @Override
    public PmsGroupBuying create(PmsGroupBuying pmsGroupBuying) {
        if(pmsGroupBuying == null)
            return null;
        pmsGroupBuying.setCreationTime(new Date());
        pmsGroupBuying.insertOrUpdate();
        if(pmsGroupBuying.getStatus().equals(CONSTANT_GROUP_STATUS_OPEN)){
            //Open the goods group
            updateProduct(Long.parseLong(pmsGroupBuying.getProductId()),CONSTANT_GROUP_TRUE);
        }else if(pmsGroupBuying.getStatus().equals(CONSTANT_GROUP_STATUS_END)){
            //Close the cluster of goods
            updateProduct(Long.parseLong(pmsGroupBuying.getProductId()),CONSTANT_GROUP_FALSE);
        }
        return pmsGroupBuying;
    }

    @Override
    public PmsGroupBuying update(PmsGroupBuying pmsGroupBuying) {
        if(pmsGroupBuying == null)
            return null;
        pmsGroupBuying.updateById();
        pmsGroupBuying = pmsGroupBuyingMapper.selectById(pmsGroupBuying.getId());
        if(pmsGroupBuying.getStatus().equals(CONSTANT_GROUP_STATUS_OPEN)){
            //Open the goods group
            updateProduct(Long.parseLong(pmsGroupBuying.getProductId()),CONSTANT_GROUP_TRUE);
        }else if(pmsGroupBuying.getStatus().equals(CONSTANT_GROUP_STATUS_END)){
            //Close the cluster of goods
            updateProduct(Long.parseLong(pmsGroupBuying.getProductId()),CONSTANT_GROUP_FALSE);
        }

        return pmsGroupBuying;
    }

    /**
     * Synchronous goods group status
     * @param productId
     * @param status
     * @return
     */
    private boolean updateProduct(Long productId,Integer status){
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product != null) {
            product.setGroup(status);
            productMapper.updateByPrimaryKey(product);
            //更新商品状态到ES
            esProductService.update(productId);
            return true;
        }
        return false;
    }
}
