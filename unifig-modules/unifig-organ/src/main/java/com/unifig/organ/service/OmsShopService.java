package com.unifig.organ.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.domain.EsShop;
import com.unifig.organ.model.OmsShop;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.result.Rest;
import com.unifig.result.ResultData;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 店铺表 服务类
 * </p>
 *
 *
 * @since 2019-03-11
 */
public interface OmsShopService extends IService<OmsShop> {

    OmsShop save(OmsShop omsShop);

    OmsShop updateShop(OmsShop omsShop);

    OmsShop selectByShopId(String id);

    Page<OmsShop> selectShopList(Integer page, Integer rows, String terraceId, String name);

    Page<EsShop> EsSelectShopList(Integer page, Integer rows, String terraceId, String keyword, BigDecimal longitude, BigDecimal latitude,BigDecimal distance);

    ResultData importAll();

    Rest<EsShop> selectESById(String id, BigDecimal longitude, BigDecimal latitude);

    ResultData deleteShop(String id);

    ResultData openShop(String ids);

    Map<String,String> shopDistance(BigDecimal longitude, BigDecimal latitude, BigDecimal distance);
}
