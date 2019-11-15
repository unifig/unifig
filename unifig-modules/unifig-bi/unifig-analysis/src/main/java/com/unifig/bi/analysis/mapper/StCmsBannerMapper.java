package com.unifig.bi.analysis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.bi.analysis.model.StCmsBanner;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * banner Mapper 接口
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StCmsBannerMapper extends BaseMapper<StCmsBanner> {

    List<Map<String, Object>> lineDay(List<Map<String, Object>> paremList);

    List<Map<String, Object>> line(List<Map<String, Object>> paremList);

}
