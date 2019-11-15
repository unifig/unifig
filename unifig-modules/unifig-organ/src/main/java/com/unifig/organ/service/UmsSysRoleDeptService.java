package com.unifig.organ.service;

import java.util.List;


/**
 * 角色与部门对应关系
 *

 * @date 2018-10-24
 */
public interface UmsSysRoleDeptService {

    void saveOrUpdate(Long roleId, List<Long> deptIdList);

    /**
     * 根据角色ID，获取部门ID列表
     */
    List<Long> queryDeptIdList(Long roleId);

    /**
     * 根据用户ID获取权限部门列表
     *
     * @param userId
     * @return
     */
    List<Long> queryDeptIdListByUserId(Long userId);
}
