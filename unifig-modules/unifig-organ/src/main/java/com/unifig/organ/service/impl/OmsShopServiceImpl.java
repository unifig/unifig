package com.unifig.organ.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.domain.EsShop;
import com.unifig.organ.model.OmsShop;
import com.unifig.organ.mapper.OmsShopMapper;
import com.unifig.organ.model.OmsShopStaff;
import com.unifig.organ.repository.EsShopRepository;
import com.unifig.organ.service.OmsShopService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.organ.service.OmsShopStaffService;
import com.unifig.organ.service.UserService;
import com.unifig.result.Rest;
import com.unifig.result.ResultData;
import com.unifig.utils.GeoUtil;
import com.unifig.utils.kartor.KartorUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 店铺表 服务实现类
 * </p>
 *
 *
 * @since 2019-03-11
 */
@Service
@Slf4j
public class OmsShopServiceImpl extends ServiceImpl<OmsShopMapper, OmsShop> implements OmsShopService {

    @Autowired
    private OmsShopMapper omsShopMapper;
    @Autowired
    private EsShopRepository esShopRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private OmsShopStaffService omsShopStaffService;

    @Autowired
    private UserService userService;

    @Override
    public OmsShop save(OmsShop omsShop) {
        omsShop.setCreateTime(new Date());
        omsShopMapper.insert(omsShop);

        //新增或者修改员工信息
        if (omsShop.getShopStaffLists() != null) {
            for (OmsShopStaff omsShopStaff : omsShop.getShopStaffLists()) {
                omsShopStaff.setCreateTime(new Date());
                omsShopStaff.setShopId(omsShop.getId());
                String openId = KartorUtils.createKartorUser(omsShopStaff.getAccountNumber().toString());
                if (openId == null) {
                    return null;
                }
                int i = userService.updateUserShopId(omsShopStaff.getUserId(), omsShopStaff.getShopId(),openId);
                //用户存在,创建用户和店铺关联数据
                if(i != 0){
                    omsShopStaffService.insertOrUpdate(omsShopStaff);
                }
            }
        }

        //保存ES中店铺数据
        EsShop esShop = new EsShop();
        BeanUtil.copyProperties(omsShop, esShop);
        String location = omsShop.getLatitude()+","+ omsShop.getLongitude();
        esShop.setLocation(location);
        esShopRepository.save(esShop);
        return omsShop;
    }

    @Override
    public OmsShop updateShop(OmsShop omsShop) {
        omsShopMapper.updateById(omsShop);

        //新增或者修改员工信息
        if (omsShop.getShopStaffLists() != null) {
            for (OmsShopStaff omsShopStaff : omsShop.getShopStaffLists()) {
                if(omsShopStaff.getId() == null){
                    omsShopStaff.setCreateTime(new Date());
                }
//                int i = userService.updateUserShopId(omsShopStaff.getUserId(), omsShopStaff.getShopId());
                //用户存在,创建用户和店铺关联数据
//                if(i != 0){
//                    omsShopStaffService.insertOrUpdate(omsShopStaff);
//                }
                omsShopStaff.setShopId(omsShop.getId());
                omsShopStaffService.insertOrUpdate(omsShopStaff);
            }
        }

        //更新ES中店铺数据
        esShopRepository.delete(omsShop.getId());
        EsShop esShop = new EsShop();
        BeanUtil.copyProperties(omsShopMapper.selectById(omsShop.getId()), esShop);
        String location = omsShop.getLatitude()+","+ omsShop.getLongitude();
        esShop.setLocation(location);
        esShopRepository.save(esShop);
        return omsShop;
    }

    @Override
    public OmsShop selectByShopId(String id) {
        return omsShopMapper.selectById(id);
    }

    @Override
    public Page<OmsShop> selectShopList(Integer page, Integer rows, String terraceId, String name) {
        Page<OmsShop> umsShopPage = new Page<>();
        EntityWrapper<OmsShop> wrapper = new EntityWrapper<OmsShop>();
        if (terraceId != null) {
            wrapper.eq("terrace_id",terraceId);
        }
        if (name != null) {
            wrapper.like("name",name);
        }
        List<OmsShop> omsShops = omsShopMapper.selectPage(new Page<OmsShop>(page, rows), wrapper);
        Integer integer = omsShopMapper.selectCount(wrapper);
        umsShopPage.setRecords(omsShops);
        umsShopPage.setTotal(integer);
        return umsShopPage;
    }

    @Override
    public Page<EsShop> EsSelectShopList(Integer page, Integer rows, String terraceId, String keyword, BigDecimal longitude, BigDecimal latitude,BigDecimal distance) {
        Page<EsShop> umsShopPage = new Page<>();
        Pageable pageable = new PageRequest(page, rows);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤
        if (terraceId != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (terraceId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("terraceId", terraceId));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }
        //搜索
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(10))
                .add(QueryBuilders.matchQuery("site", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(5))
                .scoreMode("sum")
                .setMinScore(2);
        if(distance != null ){
            GeoDistanceQueryBuilder builder =
                    QueryBuilders.geoDistanceQuery("location")//查询字段
                            .point(latitude.doubleValue(), longitude.doubleValue())//设置经纬度
                            .distance(distance.doubleValue(), DistanceUnit.KILOMETERS)//设置距离和单位（米）
                            .geoDistance(GeoDistance.ARC);
            GeoDistanceSortBuilder sortBuilder =
                    SortBuilders.geoDistanceSort("location")
                            .point(latitude.doubleValue(), longitude.doubleValue())
                            .unit(DistanceUnit.KILOMETERS)
                            .order(SortOrder.ASC);//排序方式
            nativeSearchQueryBuilder.withFilter(builder);
            nativeSearchQueryBuilder.withSort(sortBuilder);
        }
        if (StringUtils.isEmpty(keyword)) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        } else {
            nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
        }
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
//        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        log.info("DSL:{}", searchQuery.getQuery().toString());
        org.springframework.data.domain.Page<EsShop> search = esShopRepository.search(searchQuery);
        search.getContent().forEach(li -> {
            li.setDistance(GeoUtil.getDistanceOfMeter(li.getLatitude().doubleValue(),li.getLongitude().doubleValue(),latitude.doubleValue(),longitude.doubleValue()));
        });
        umsShopPage.setRecords(search.getContent());
        umsShopPage.setTotal(search.getTotalElements());
        return umsShopPage;
    }

    @Override
    public ResultData importAll() {
        EntityWrapper<OmsShop> wrapper = new EntityWrapper<OmsShop>();
        wrapper.eq("status","0");
        List<OmsShop> omsShops = omsShopMapper.selectList(wrapper);
        List<EsShop> list = new ArrayList<>();
        for (OmsShop omsShop : omsShops) {
            EsShop esShop = new EsShop();
            BeanUtil.copyProperties(omsShop, esShop);
            String location = omsShop.getLatitude()+","+ omsShop.getLongitude();
            esShop.setLocation(location);
            list.add(esShop);
        }
        esShopRepository.save(list);
        return ResultData.result(true);
    }

    @Override
    public Rest<EsShop> selectESById(String id, BigDecimal longitude, BigDecimal latitude) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //过滤
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("id", id));
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        log.info("DSL:{}", searchQuery.getQuery().toString());
        org.springframework.data.domain.Page<EsShop> search = esShopRepository.search(searchQuery);
        search.getContent().forEach(li -> {
            li.setDistance(GeoUtil.getDistanceOfMeter(li.getLatitude().doubleValue(),li.getLongitude().doubleValue(),latitude.doubleValue(),longitude.doubleValue()));
        });
        if(search.getContent().size()>0){
            return Rest.resultData(new EsShop()).setData(search.getContent().get(0));
        }
        return Rest.resultError();
    }

    @Override
    public ResultData deleteShop(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            OmsShop omsShop = new OmsShop();
            omsShop.setId(id);
            omsShop.setStatus(1);
            omsShop.updateById();
            //删除es中店铺信息
            esShopRepository.delete(id);
            //TODO 关闭店铺后下架全部店铺商品
        }
        return ResultData.result(true).setData(true).setMsg("关闭店铺成功");
    }

    @Override
    public ResultData openShop(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            OmsShop omsShop = new OmsShop();
            omsShop.setId(id);
            omsShop.setStatus(0);
            omsShop.updateById();
            //更新数据到es中
            EsShop esShop = new EsShop();
            OmsShop omsShopES = omsShopMapper.selectById(id);
            BeanUtil.copyProperties(omsShopES, esShop);
            String location = omsShopES.getLatitude()+","+ omsShopES.getLongitude();
            esShop.setLocation(location);
            esShopRepository.save(esShop);
        }
        return ResultData.result(true).setData(true).setMsg("开启店铺成功");
    }

    @Override
    public Map<String, String> shopDistance(BigDecimal longitude, BigDecimal latitude, BigDecimal distance) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        if(distance != null ){
            GeoDistanceQueryBuilder builder =
                    QueryBuilders.geoDistanceQuery("location")//查询字段
                            .point(latitude.doubleValue(), longitude.doubleValue())//设置经纬度
                            .distance(distance.doubleValue(), DistanceUnit.KILOMETERS)//设置距离和单位（米）
                            .geoDistance(GeoDistance.ARC);
            GeoDistanceSortBuilder sortBuilder =
                    SortBuilders.geoDistanceSort("location")
                            .point(latitude.doubleValue(), longitude.doubleValue())
                            .unit(DistanceUnit.KILOMETERS)
                            .order(SortOrder.ASC);//排序方式
            nativeSearchQueryBuilder.withFilter(builder);
            nativeSearchQueryBuilder.withSort(sortBuilder);
        }
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
        log.info("DSL:{}", searchQuery.getQuery().toString());
        org.springframework.data.domain.Page<EsShop> search = esShopRepository.search(searchQuery);
        Map<String,String> map = new HashMap<>();
        search.getContent().forEach(li -> {
            map.put(li.getId(),String.valueOf(GeoUtil.getDistanceOfMeter(li.getLatitude().doubleValue(),li.getLongitude().doubleValue(),latitude.doubleValue(),longitude.doubleValue())));
        });
        return map;
    }


}
