package com.unifig.mall.service.impl;

import com.unifig.mall.bean.domain.EsProductRelatedInfo;
import com.unifig.mall.bean.dto.PmsProductCategoryWithChildrenItem;
import com.unifig.mall.bean.model.PmsProductCategory;
import com.unifig.mall.bean.model.PmsSkuStock;
import com.unifig.mall.bean.model.PmsSkuStockExample;
import com.unifig.mall.mapper.PmsSkuStockMapper;
import com.unifig.mall.repository.EsProductRepository;
import com.unifig.mall.dao.EsProductDao;
import com.unifig.mall.bean.domain.EsProduct;
import com.unifig.mall.service.EsProductService;
import com.unifig.mall.service.PmsProductCategoryService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品搜索管理Service实现类
 *    on 2018/6/19.
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);
    @Autowired
    private EsProductDao productDao;
    @Autowired
    private EsProductRepository productRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private PmsProductCategoryService productCategoryService;

    @Autowired
    private PmsSkuStockMapper skuStockMapper;



    @Override
    public int importAll() {
        List<EsProduct> esProductList = productDao.getAllEsProductList(null);
        Iterable<EsProduct> esProductIterable = productRepository.save(esProductList);
        Iterator<EsProduct> iterator = esProductIterable.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public void delete(Long id) {
        productRepository.delete(id);
    }

    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            result = productRepository.save(esProduct);
        }
        return result;
    }

    @Override
    public EsProduct update(Long id) {
        productRepository.delete(id);
        EsProduct result = null;
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            //团购商品添加团购价格
            if(esProduct.getGroup().toString().equals("1")){
                PmsSkuStockExample pmsSkuStockExample = new PmsSkuStockExample();
                PmsSkuStockExample.Criteria criteria = pmsSkuStockExample.createCriteria();
                criteria.andProductIdEqualTo(id);
                List<PmsSkuStock> pmsSkuStocks = skuStockMapper.selectByExample(pmsSkuStockExample);
                BigDecimal groupPurchasePrice = new BigDecimal(0.00);
                if(pmsSkuStocks.size()>0){
                    groupPurchasePrice = pmsSkuStocks.get(0).getGroupPurchasePrice();
                    for (PmsSkuStock pmsSkuStock : pmsSkuStocks) {
                        if(pmsSkuStock.getGroupPurchasePrice().compareTo(groupPurchasePrice) < 0){
                            groupPurchasePrice = pmsSkuStock.getGroupPurchasePrice();
                        }
                    }
                }
                esProduct.setGroupPurchasePrice(groupPurchasePrice);
            }
            result = productRepository.save(esProduct);
        }
        return result;
    }

    @Override
    public void delete(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            List<EsProduct> esProductList = new ArrayList<>();
            for (Long id : ids) {
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                esProductList.add(esProduct);
            }
            productRepository.delete(esProductList);
        }
    }

    @Override
    public void delete() {
        productRepository.deleteAll();
    }

    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        return productRepository.findByNameOrSubTitleOrKeywords(keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<EsProduct> search(Integer recommandStatus, Integer type, Long id, Integer newStatus, Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤
        if (recommandStatus != null || type != null || id != null || newStatus !=null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (recommandStatus != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("recommandStatus", recommandStatus));
            }
            if (type != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("type", type));
            }
            if (id != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("id", id));
            }
            if (newStatus != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("newStatus", newStatus));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
        return productRepository.search(searchQuery);
    }

    @Override
    public Page<EsProduct> search(Long id) {
//        Pageable pageable = new PageRequest(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //过滤
        if ( id != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (id != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("id", id));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
        return productRepository.search(searchQuery);
    }

    @Override
    public Page<EsProduct> search(Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
        return productRepository.search(searchQuery);
    }

    @Override
    public Page<EsProduct> search(String keyword, Long brandId, String productCategoryId, Integer pageNum, Integer pageSize,Integer sort) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤
        if (brandId != null || productCategoryId != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (brandId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandId", brandId));
            }
            if (productCategoryId != null) {
                //支持一二级分类查询
                boolQueryBuilder.should(QueryBuilders.termQuery("productCategoryId", productCategoryId));
                List<PmsProductCategoryWithChildrenItem> list = productCategoryService.listWithChildren();
                List<PmsProductCategoryWithChildrenItem> collect = list.stream().filter(li -> li.getId().toString().equals(productCategoryId)).collect(Collectors.toList());
                if (collect.size()>0) {
                    List<PmsProductCategory> children = collect.get(0).getChildren();
                    for (PmsProductCategory child : children) {
                        boolQueryBuilder.should(QueryBuilders.termQuery("productCategoryId", child.getId()));
                    }
                }
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }
        //搜索
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(10))
                .add(QueryBuilders.matchQuery("subTitle", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(5))
                .add(QueryBuilders.matchQuery("keywords", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(2))
                .scoreMode("sum")
                .setMinScore(2);
        if (StringUtils.isEmpty(keyword) || keyword.equals("")) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        } else {
            nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
        }
        //排序
        if(sort==1){
            //按新品从新到旧
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
        }else if(sort==2){
            //按销量从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sale").order(SortOrder.DESC));
        }else if(sort==3){
            //按价格从低到高
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        }else if(sort==4){
            //按价格从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        }else if(sort !=0){
            //按相关度
            nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
            nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
            nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();

        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
        return productRepository.search(searchQuery);
    }

    @Override
    public Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        List<EsProduct> esProductList = productDao.getAllEsProductList(id);
        if (esProductList.size() > 0) {
            EsProduct esProduct = esProductList.get(0);
            String keyword = esProduct.getName();
            Long brandId = esProduct.getBrandId();
            Long productCategoryId = esProduct.getProductCategoryId();
            //根据商品标题、品牌、分类进行搜索
            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                    .add(QueryBuilders.matchQuery("name",keyword),ScoreFunctionBuilders.weightFactorFunction(8))
                    .add(QueryBuilders.matchQuery("subTitle",keyword),ScoreFunctionBuilders.weightFactorFunction(2))
                    .add(QueryBuilders.matchQuery("keywords",keyword),ScoreFunctionBuilders.weightFactorFunction(2))
                    .add(QueryBuilders.termQuery("brandId",brandId),ScoreFunctionBuilders.weightFactorFunction(10))
                    .add(QueryBuilders.matchQuery("productCategoryId",productCategoryId),ScoreFunctionBuilders.weightFactorFunction(6))
                    .scoreMode("sum")
                    .setMinScore(2);
            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
            builder.withQuery(functionScoreQueryBuilder);
            builder.withPageable(pageable);
            NativeSearchQuery searchQuery = builder.build();
            LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
            return productRepository.search(searchQuery);
        }
        return new PageImpl<>(null);
    }

    @Override
    public EsProductRelatedInfo searchRelatedInfo(String keyword) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //搜索条件
        if(StringUtils.isEmpty(keyword)){
            builder.withQuery(QueryBuilders.matchAllQuery());
        }else{
            builder.withQuery(QueryBuilders.multiMatchQuery(keyword,"name","subTitle","keywords"));
        }
        //聚合搜索品牌名称
        builder.addAggregation(AggregationBuilders.terms("brandNames").field("brandName"));
        //集合搜索分类名称
        builder.addAggregation(AggregationBuilders.terms("productCategoryNames").field("productCategoryName"));
        //聚合搜索商品属性，去除type=1的属性
        AbstractAggregationBuilder aggregationBuilder = AggregationBuilders.nested("allAttrValues")
                .path("attrValueList")
                .subAggregation(AggregationBuilders.filter("productAttrs")
                .filter(QueryBuilders.termQuery("attrValueList.type",1))
                .subAggregation(AggregationBuilders.terms("attrIds")
                        .field("attrValueList.productAttributeId")
                        .subAggregation(AggregationBuilders.terms("attrValues")
                                .field("attrValueList.value"))
                        .subAggregation(AggregationBuilders.terms("attrNames")
                                .field("attrValueList.name"))));
        builder.addAggregation(aggregationBuilder);
        NativeSearchQuery searchQuery = builder.build();
        return elasticsearchTemplate.query(searchQuery, response -> {
            LOGGER.info("DSL:{}",searchQuery.getQuery().toString());
            return convertProductRelatedInfo(response);
        });
    }

    /**
     * 将返回结果转换为对象
     */
    private EsProductRelatedInfo convertProductRelatedInfo(SearchResponse response) {
        EsProductRelatedInfo productRelatedInfo = new EsProductRelatedInfo();
        Map<String, Aggregation> aggregationMap = response.getAggregations().getAsMap();
        //设置品牌
        Aggregation brandNames = aggregationMap.get("brandNames");
        List<String> brandNameList = new ArrayList<>();
        for(int i = 0; i<((StringTerms) brandNames).getBuckets().size(); i++){
            brandNameList.add(((StringTerms) brandNames).getBuckets().get(i).getKeyAsString());
        }
        productRelatedInfo.setBrandNames(brandNameList);
        //设置分类
        Aggregation productCategoryNames = aggregationMap.get("productCategoryNames");
        List<String> productCategoryNameList = new ArrayList<>();
        for(int i=0;i<((StringTerms) productCategoryNames).getBuckets().size();i++){
            productCategoryNameList.add(((StringTerms) productCategoryNames).getBuckets().get(i).getKeyAsString());
        }
        productRelatedInfo.setProductCategoryNames(productCategoryNameList);
        //设置参数
        Aggregation productAttrs = aggregationMap.get("allAttrValues");
        List<Terms.Bucket> attrIds = ((LongTerms) ((InternalFilter)productAttrs.getProperty("productAttrs")).getAggregations().getProperty("attrIds")).getBuckets();
        List<EsProductRelatedInfo.ProductAttr> attrList = new ArrayList<>();
        for (Terms.Bucket attrId : attrIds) {
            EsProductRelatedInfo.ProductAttr attr = new EsProductRelatedInfo.ProductAttr();
            attr.setAttrId((Long) attrId.getKey());
            List<String> attrValueList = new ArrayList<>();
            List<Terms.Bucket> attrValues = ((StringTerms) attrId.getAggregations().get("attrValues")).getBuckets();
            List<Terms.Bucket> attrNames = ((StringTerms) attrId.getAggregations().get("attrNames")).getBuckets();
            for (Terms.Bucket attrValue : attrValues) {
                attrValueList.add(attrValue.getKeyAsString());
            }
            attr.setAttrValues(attrValueList);
            if(!CollectionUtils.isEmpty(attrNames)){
                String attrName = attrNames.get(0).getKeyAsString();
                attr.setAttrName(attrName);
            }
            attrList.add(attr);
        }
        productRelatedInfo.setProductAttrs(attrList);
        return productRelatedInfo;
    }

    @Override
    public Page<EsProduct> search(Integer recommandStatus, Integer type,Integer newStatus, Integer pageNum, Integer pageSize,String terraceId,String shopId) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤
        if (recommandStatus != null || type != null || newStatus !=null ||terraceId != null || shopId != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (recommandStatus != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("recommandStatus", recommandStatus));
            }
            if (type != null) {
                if(type==3){
                    boolQueryBuilder.must(QueryBuilders.termQuery("group", 1));
                }else{
                    boolQueryBuilder.must(QueryBuilders.termQuery("type", type));
                }
            }
            if (newStatus != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("newStatus", newStatus));
            }
            if (terraceId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("terraceId", terraceId));
            }
            if (shopId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("shopId", shopId));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        LOGGER.info("DSL:{}", searchQuery.getQuery().toString());
        return productRepository.search(searchQuery);
    }
}
