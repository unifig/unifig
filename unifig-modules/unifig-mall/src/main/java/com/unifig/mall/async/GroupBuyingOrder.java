/**
 * FileName: CloseGroupBuyingOrder
 * Author:
 * Date:     2019/7/17 17:19
 * Description: 异步关闭团购订单
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.async;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.unifig.mall.mapper.OmsOrderMapper;
import com.unifig.mall.mapper.PmsGroupBuyingUserMapper;
import com.unifig.mall.service.OmsPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h3>概要:</h3><p>CloseGroupBuyingOrder</p>
 * <h3>功能:</h3><p>团购订单异步类</p>
 *
 * @create 2019/7/17
 * @since 1.0.0
 */
@Component
@Slf4j
public class GroupBuyingOrder {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private PmsGroupBuyingUserMapper pmsGroupBuyingUserMapper;

    /**
     * 拼团成功,更改订单状态为待发货
     * @param goupBuyingId
     */
    @Async
    public void updateOrder(String goupBuyingId) {
        EntityWrapper<PmsGroupBuyingUser> wrapper = new EntityWrapper<PmsGroupBuyingUser>();
        wrapper.eq("parent_id",goupBuyingId);
        List<PmsGroupBuyingUser> pmsGroupBuyingUsers = pmsGroupBuyingUserMapper.selectList(wrapper);
        log.info("正在处理团购记录数量{}",pmsGroupBuyingUsers.size());
        pmsGroupBuyingUsers.forEach(li->{
            log.info("正在处理订单,订单id{}",li.getOrderId());
            OmsOrder omsOrder = orderMapper.selectByPrimaryKey(Long.valueOf(li.getOrderId()));
            //更改订单状态为待发货
            omsOrder.setStatus(1);
            orderMapper.updateByPrimaryKey(omsOrder);
            log.info("处理完成,订单id{},编号{}状态更改为待发货",omsOrder.getId(),omsOrder.getOrderSn());
        });

    }

}
