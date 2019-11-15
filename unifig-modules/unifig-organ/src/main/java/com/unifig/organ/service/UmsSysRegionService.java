package com.unifig.organ.service;


import com.baomidou.mybatisplus.service.IService;
import com.unifig.organ.model.SysRegionEntity;

import java.util.List;
import java.util.Map;

/**
 * Service接口
 *

 * @date 2018-10-24
 */
public interface UmsSysRegionService extends IService<SysRegionEntity> {

    /**
     * 根据主键查询实体
     *
     * @param id 主键
     * @return 实体
     */
    SysRegionEntity queryObject(Integer id);

    /**
     * 分页查询
     *
     * @param map 参数
     * @return list
     */
    List<SysRegionEntity> queryList(Map<String, Object> map);

    /**
     * 分页统计总数
     *
     * @param map 参数
     * @return 总数
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 保存实体
     *
     * @param region 实体
     * @return 保存条数
     */
    int save(SysRegionEntity region);

    /**
     * 根据主键更新实体
     *
     * @param region 实体
     * @return 更新条数
     */
    int update(SysRegionEntity region);

    /**
     * 根据主键删除
     *
     * @param id
     * @return 删除条数
     */
    int delete(Integer id);

    /**
     * 根据主键批量删除
     *
     * @param ids
     * @return 删除条数
     */
    int deleteBatch(Integer[] ids);

    /**
     * 根据类型查询
     *
     * @param type
     * @return
     */
    List<SysRegionEntity> selectByType(int type);
}
