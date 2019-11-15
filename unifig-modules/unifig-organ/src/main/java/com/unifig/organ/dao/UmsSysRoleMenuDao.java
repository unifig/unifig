package com.unifig.organ.dao;

import com.unifig.organ.model.SysRoleMenuEntity;
import com.unifig.dao.BaseDao;

import java.util.List;

/**
 * 角色与菜单对应关系
 *

 * @date 2018-10-24
 */
public interface UmsSysRoleMenuDao extends BaseDao<SysRoleMenuEntity> {

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(Long roleId);
}
