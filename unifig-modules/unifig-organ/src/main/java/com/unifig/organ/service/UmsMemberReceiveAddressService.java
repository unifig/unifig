package com.unifig.organ.service;

import com.unifig.organ.model.UmsMemberReceiveAddress;

import java.util.List;

/**
 * 用户地址管理Service
 *    on 2018/8/28.
 */
public interface UmsMemberReceiveAddressService {
    /**
     * 添加收货地址
     */
    int add(UmsMemberReceiveAddress address,String userId);

    /**
     * 删除收货地址
     *
     * @param id 地址表的id
     */
    int delete(Long id,String userId);

    /**
     * 修改收货地址
     *
     * @param id      地址表的id
     * @param address 修改的收货地址信息
     */
    int update(Long id, UmsMemberReceiveAddress address,String userId);

    /**
     * 返回当前用户的收货地址
     */
    List<UmsMemberReceiveAddress> list(String userId);

    /**
     * 获取地址详情
     *
     * @param id 地址id
     */
    UmsMemberReceiveAddress getItem(Long id,String userId);

    /**
     * 设为默认收货地址
     * @param id
     * @param userId
     * @return
     */
    UmsMemberReceiveAddress defaultStatus(Long id, String userId);
}
