package com.unifig.organ.dao;

import com.unifig.organ.model.SysUserRoleEntity;
import com.unifig.dao.BaseDao;

import java.util.List;

/**
 * 用户与角色对应关系
 *

 * @date 2018-10-24
 */
public interface UmsSysUserRoleDao extends BaseDao<SysUserRoleEntity> {

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(Long userId);
}
