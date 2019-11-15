package com.unifig.bi.analysis.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.bi.analysis.model.StSmsPromotion;

import java.sql.Date;
import java.util.Map;

/**
 * <p>
 * 活动类表 服务类
 * </p>
 *
 *
 * @since 2019-03-21
 */
public interface StSmsPromotionService extends IService<StSmsPromotion> {
    Map<String, Object> line(Date startDate, Date stopDate, String promotionId);
}
