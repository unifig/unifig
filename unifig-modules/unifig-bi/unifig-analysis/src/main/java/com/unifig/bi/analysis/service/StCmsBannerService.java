package com.unifig.bi.analysis.service;

import com.unifig.bi.analysis.model.StCmsBanner;
import com.baomidou.mybatisplus.service.IService;

import java.sql.Date;
import java.util.Map;

/**
 * <p>
 * banner 服务类
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StCmsBannerService extends IService<StCmsBanner> {

    Map<String, Object> line(Date startDate, Date stopDate, String bannerId);
}
