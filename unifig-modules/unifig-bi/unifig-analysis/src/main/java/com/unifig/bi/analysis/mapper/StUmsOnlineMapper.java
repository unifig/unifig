package com.unifig.bi.analysis.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.bi.analysis.model.StUmsOnline;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章分类表 Mapper 接口
 * </p>
 *
 *
 * @since 2019-03-21
 */
public interface StUmsOnlineMapper extends BaseMapper<StUmsOnline> {


    List<Map<String, Object>> countDayReport(Map<String, Object> dayReportMap);

    List<Map<String, Object>> countYearReport(Map<String, Object> yearReportMap);

    List<Map<String, Object>> countMonthReport(Map<String, Object> yearReportMap);

    List<Map<String, Object>> countWeekReport(Map<String, Object> yearReportMap);



}
