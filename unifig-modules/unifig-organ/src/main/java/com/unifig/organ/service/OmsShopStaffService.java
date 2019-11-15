package com.unifig.organ.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.organ.model.OmsShopStaff;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 店铺员工表 服务类
 * </p>
 *
 *
 * @since 2019-03-11
 */
public interface OmsShopStaffService extends IService<OmsShopStaff> {

    OmsShopStaff save(OmsShopStaff OmsShopStaff);

    OmsShopStaff updateShop(OmsShopStaff OmsShopStaff);

    OmsShopStaff selectByShopId(String id);

    Page<OmsShopStaff> selectShopList(Integer page, Integer rows, String shopId);

    List<OmsShopStaff> saveList(List<OmsShopStaff> omsShopStaff);

    String selectByUserId(String id);
}
