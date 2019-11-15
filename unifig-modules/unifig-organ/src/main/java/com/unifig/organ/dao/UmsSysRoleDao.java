package com.unifig.organ.dao;

import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysRoleEntity;
import com.unifig.dao.BaseDao;

import java.util.List;

/**
 * 角色管理
 *

 * @date 2018-10-24
 */
public interface UmsSysRoleDao extends BaseDao<SysRoleEntity> {

    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(Long createUserId);

    /**
     * 查询角色审批选择范围
     *
     * @return
     */
    List<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto);
}
