package com.unifig.bi.analysis.service;

import com.unifig.bi.analysis.model.StSmsNavigation;
import com.baomidou.mybatisplus.service.IService;

import java.sql.Date;
import java.util.Map;

/**
 * <p>
 * 导航栏 服务类
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StSmsNavigationService extends IService<StSmsNavigation> {

    Map<String, Object> line(Date startDate, Date stopDate, String navigationId);

}
