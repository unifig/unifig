package com.unifig.bi.analysis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.bi.analysis.model.StSmsPromotion;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动 Mapper 接口
 * </p>
 *
 *
 * @since 2019-03-21
 */
public interface StSmsPromotionMapper extends BaseMapper<StSmsPromotion> {

    List<Map<String, Object>> lineDay(List<Map<String, Object>> paremList);

    List<Map<String, Object>> line(List<Map<String, Object>> paremList);

}
