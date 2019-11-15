package com.unifig.organ.service;

import com.unifig.organ.model.SysLogEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统日志
 *

 * @date 2018-10-24
 */
public interface UmsSysLogService {

    SysLogEntity queryObject(Long id);

    List<SysLogEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysLogEntity sysLog);

    void update(SysLogEntity sysLog);

    void delete(Long id);

    void deleteBatch(Long[] ids);
}
