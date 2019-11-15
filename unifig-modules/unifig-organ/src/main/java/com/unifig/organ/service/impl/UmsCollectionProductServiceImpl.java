package com.unifig.organ.service.impl;

import com.unifig.organ.domain.UmsMemberCollectionProduct;
import com.unifig.organ.repository.UmsMemberCollectionProductRepository;
import com.unifig.organ.service.UmsCollectionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员收藏Service实现类
 *    on 2018/8/2.
 */
@Service
public class UmsCollectionProductServiceImpl implements UmsCollectionProductService {
    @Autowired
    private UmsMemberCollectionProductRepository productCollectionRepository;

    @Override
    public int addProduct(UmsMemberCollectionProduct productCollection) {
        int count = 0;
        UmsMemberCollectionProduct findCollection = productCollectionRepository.findByMemberIdAndProductId(productCollection.getMemberId(), productCollection.getProductId());
        if (findCollection == null) {
            productCollectionRepository.save(productCollection);
            count = 1;
        }
        return count;
    }

    @Override
    public int deleteProduct(Long memberId, Long productId) {
        return productCollectionRepository.deleteByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public List<UmsMemberCollectionProduct> listProduct(Long memberId) {
        return productCollectionRepository.findByMemberId(memberId);
    }

    @Override
    public List<UmsMemberCollectionProduct> listProductIndex(Long memberId) {
        List<UmsMemberCollectionProduct> umsMemberProductCollections = productCollectionRepository.findByMemberId(memberId);
        if (umsMemberProductCollections.size() > 3) {
            return umsMemberProductCollections.subList(0, 2);
        }
        return umsMemberProductCollections;
    }

    @Override
    public long countProduct(Long memberId) {
        long count = productCollectionRepository.countByMemberId(memberId);
        return count;
    }

    @Override
    public UmsMemberCollectionProduct selectByProductId(Long id, Long userId) {
        UmsMemberCollectionProduct byMemberIdAndProductId = productCollectionRepository.findByMemberIdAndProductId(userId, id);
        return byMemberIdAndProductId;
    }
}
