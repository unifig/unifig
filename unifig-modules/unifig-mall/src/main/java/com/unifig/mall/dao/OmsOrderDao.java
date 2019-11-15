package com.unifig.mall.dao;

import com.unifig.mall.bean.dto.OmsOrderDeliveryParam;
import com.unifig.mall.bean.dto.OmsOrderDetail;
import com.unifig.mall.bean.dto.OmsOrderQueryParam;
import com.unifig.mall.bean.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单自定义查询Dao
 *    on 2018/10/12.
 */
public interface OmsOrderDao {
    /**
     * 条件查询订单
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("id") Long id);
}
