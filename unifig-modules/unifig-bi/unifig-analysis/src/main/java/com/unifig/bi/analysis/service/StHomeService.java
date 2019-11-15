package com.unifig.bi.analysis.service;

import com.unifig.bi.analysis.vo.StReportVo;

import java.util.Map;

/**
 * <p>
 * 首页 服务类
 * </p>
 *
 *
 * @since 2019-03-21
 */
public interface StHomeService {

    Map<String, Object> overview(String deptId);

    Map<String, StReportVo> report(String deptId, String type);
}
