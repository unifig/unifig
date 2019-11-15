package com.unifig.organ.repository;

import com.unifig.organ.domain.EsShop;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 店铺ES操作类
 * Created by   on 2019/03/12.
 */
public interface EsShopRepository extends ElasticsearchRepository<EsShop, String> {
//    /**
//     * 搜索查询
//     *
//     * @param name              商品名称
//     * @param subTitle          商品标题
//     * @param keywords          商品关键字
//     * @param page              分页信息
//     * @return
//     */
//    Page<EsProduct> findByNameOrSubTitleOrKeywords(String name, String subTitle, String keywords, Pageable page);

}
