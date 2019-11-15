package com.unifig.organ.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.organ.model.SysRegionEntity;

import java.util.List;
import java.util.Map;

/**

 * @date 2018-10-24
 */
public interface UmsSysRegionDao extends BaseMapper<SysRegionEntity> {
    int save(SysRegionEntity t);

    void save(Map<String, Object> map);

    void saveBatch(List<SysRegionEntity> list);

    int deleteBatch(Object[] id);

    SysRegionEntity queryObject(Object id);

    List<SysRegionEntity> queryList(Map<String, Object> map);

    List<SysRegionEntity> queryList(Object id);

    int queryTotal(Map<String, Object> map);

    int queryTotal();

}
