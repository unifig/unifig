package com.unifig.bi.analysis.mapper;

import com.unifig.bi.analysis.model.StSmsVippage;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专享 Mapper 接口
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StSmsVippageMapper extends BaseMapper<StSmsVippage> {

    List<Map<String, Object>> lineDay(List<Map<String, Object>> paremList);

    List<Map<String, Object>> line(List<Map<String, Object>> paremList);

}
