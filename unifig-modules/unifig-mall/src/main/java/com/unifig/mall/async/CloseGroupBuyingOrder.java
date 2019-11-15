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
import com.unifig.mall.bean.model.OmsOrderExample;
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
 * <h3>功能:</h3><p>异步关闭团购订单</p>
 *
 * @create 2019/7/17
 * @since 1.0.0
 */
@Component
@Slf4j
public class CloseGroupBuyingOrder {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsPayService omsPayService;

    @Autowired
    private PmsGroupBuyingUserMapper pmsGroupBuyingUserMapper;

    /**
     * 关闭团购订单并退款
     * @param goupBuyingId
     */
    @Async
    public void close(String goupBuyingId) {
        log.debug("开始关闭团购失败");
        EntityWrapper<PmsGroupBuyingUser> wrapper = new EntityWrapper<PmsGroupBuyingUser>();
        wrapper.eq("parent_id",goupBuyingId);
        List<PmsGroupBuyingUser> pmsGroupBuyingUsers = pmsGroupBuyingUserMapper.selectList(wrapper);
        pmsGroupBuyingUsers.forEach(li->{
            OmsOrder omsOrder = orderMapper.selectByPrimaryKey(Long.valueOf(li.getOrderId()));
            //关闭拼团失败订单
            //退回已付款用户付款金额
            omsPayService.refund(omsOrder.getId(),null);
        });
;
    }

    /**
     * 关闭团购订单并退款
     * @param orderId
     */
    @Async
    public void closeOrder(String orderId) {
        log.info("开始关闭拼团订单,订单id{}",orderId);
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
        //关闭拼团失败订单
        //退回已付款用户付款金额
        omsPayService.refund(omsOrder.getId(),null);
    }

}
