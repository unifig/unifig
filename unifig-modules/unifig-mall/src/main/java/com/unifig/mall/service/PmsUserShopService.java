package com.unifig.mall.service;

import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.model.PmsUserShop;
import com.baomidou.mybatisplus.service.IService;
import com.unifig.result.ResultData;

/**
 * <p>
 * 我的店铺列表 服务类
 * </p>
 *
 *
 * @since 2019-02-19
 */
public interface PmsUserShopService extends IService<PmsUserShop> {

    PmsUserShop create(PmsUserShop pmsUserShop);

    PmsUserShop remove(PmsUserShop pmsUserShop);

    ResultData selectProductList(Integer page, Integer rows,UserCache userCache);

    ResultData selectMyProductList(Integer page, Integer rows, UserCache userCache);
}
