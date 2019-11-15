package com.unifig.mall.component;

import com.unifig.mall.service.SmsCouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *    on 2018/8/24.
 * 优惠券过期关闭定时器
 */
@Component
public class CouponTimeOutCancelTask {
    private Logger LOGGER =LoggerFactory.getLogger(CouponTimeOutCancelTask.class);
    @Autowired
    private SmsCouponService smsCouponService;

    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每10分钟扫描一次，扫描设定超时时间之前下的订单，如果没支付则取消该订单
     */
    @Scheduled(cron = "0 0/10 * ? * ?")
    public void cancelTimeOutOrder(){
        smsCouponService.staleDated();
        LOGGER.info("关闭过期优惠券");
    }
}
