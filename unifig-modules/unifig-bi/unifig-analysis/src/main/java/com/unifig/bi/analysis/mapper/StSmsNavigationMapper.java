package com.unifig.bi.analysis.mapper;

import com.unifig.bi.analysis.model.StSmsNavigation;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 导航栏 Mapper 接口
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StSmsNavigationMapper extends BaseMapper<StSmsNavigation> {

    List<Map<String, Object>> lineDay(List<Map<String, Object>> paremList);

    List<Map<String, Object>> line(List<Map<String, Object>> paremList);
}
