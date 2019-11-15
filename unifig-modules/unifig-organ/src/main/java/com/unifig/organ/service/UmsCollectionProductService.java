package com.unifig.organ.service;

import com.unifig.organ.domain.UmsMemberCollectionProduct;

import java.util.List;

/**
 * 会员收藏Service
 *    on 2018/8/2.
 */
public interface UmsCollectionProductService {
    int addProduct(UmsMemberCollectionProduct productCollection);

    int deleteProduct(Long memberId, Long productId);

    List<UmsMemberCollectionProduct> listProduct(Long memberId);

    List<UmsMemberCollectionProduct> listProductIndex(Long memberId);

    long countProduct(Long memberId);

    UmsMemberCollectionProduct selectByProductId(Long id, Long aLong);
}
