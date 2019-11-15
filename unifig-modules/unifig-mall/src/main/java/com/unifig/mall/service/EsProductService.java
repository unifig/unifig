package com.unifig.mall.service;

import com.unifig.mall.bean.domain.EsProduct;
import com.unifig.mall.bean.domain.EsProductRelatedInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 商品搜索管理Service
 *    on 2018/6/19.
 */
public interface EsProductService {
    /**
     * 从数据库中导入所有商品到ES
     */
    int importAll();

    /**
     * 根据id删除商品
     */
    void delete(Long id);

    /**
     * 根据id更新商品
     */
    EsProduct update(Long id);


    /**
     * 根据id创建商品
     */
    EsProduct create(Long id);

    /**
     * 批量删除商品
     */
    void delete(List<Long> ids);

    /**
     * 清空数据
     */
    void delete();

    /**
     * 根据关键字搜索名称或者副标题
     */
    Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 根据类型查询
     */
    Page<EsProduct> search(Integer recommandStatus, Integer type, Long id, Integer newStatus, Integer pageNum, Integer pageSize);

    /**
     * 根据店铺id 平台id查询
     */
    Page<EsProduct> search(Integer recommandStatus, Integer type,Integer newStatus, Integer pageNum, Integer pageSize,String terraceId,String shopId);

    /**
     * 分页查询
     */
    Page<EsProduct> search(Integer pageNum, Integer pageSize);

    /**
     * 根据id查询
     */
    Page<EsProduct> search(Long id);

    /**
     * 根据关键字搜索名称或者副标题复合查询
     */
    Page<EsProduct> search(String keyword, Long brandId, String productCategoryId, Integer pageNum, Integer pageSize,Integer sort);

    /**
     * 根据商品id推荐相关商品
     */
    Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize);

    /**
     * 获取搜索词相关品牌、分类、属性
     */
    EsProductRelatedInfo searchRelatedInfo(String keyword);
}
