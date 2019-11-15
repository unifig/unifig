package com.unifig.organ.dao;

import com.unifig.dao.BaseDao;
import com.unifig.organ.model.SysMacroEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用字典表Dao
 *

 * @date 2018-10-24
 */
public interface UmsSysMacroDao extends BaseDao<SysMacroEntity> {

    /**
     * 查询数据字段
     *
     * @param value
     * @return
     */
    List<SysMacroEntity> queryMacrosByValue(@Param("value") String value);
}
