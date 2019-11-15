package com.unifig.bi.analysis.service;

import com.baomidou.mybatisplus.service.IService;
import com.unifig.bi.analysis.model.StSmsVippage;

import java.sql.Date;
import java.util.Map;

/**
 * <p>
 * 专享 服务类
 * </p>
 *
 *
 * @since 2019-03-22
 */
public interface StSmsVippageService extends IService<StSmsVippage> {

    Map<String, Object> line(Date startDate, Date stopDate);

}
