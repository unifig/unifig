package com.unifig.organ.service;

import java.util.List;


/**
 * 用户与角色对应关系
 *

 * @date 2018-10-24
 */
public interface UmsSysUserRoleService {

    void saveOrUpdate(Long userId, List<Long> roleIdList);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(Long userId);

    void delete(Long userId);
}
