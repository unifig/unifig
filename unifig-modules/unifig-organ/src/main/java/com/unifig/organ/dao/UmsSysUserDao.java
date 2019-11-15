package com.unifig.organ.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysUserEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *

 * @date 2018-10-24
 */
public interface UmsSysUserDao extends BaseMapper<SysUserEntity> {

    /**
     * 查询用户的所有权限
     *
     * @param userId 用户ID
     */
    List<String> queryAllPerms(Long userId);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);

    /**
     * 根据用户名，查询系统用户
     */
    SysUserEntity queryByUserName(String username);

    /**
     * 修改密码
     */
    int updatePassword(Map<String, Object> map);

    /**
     * 根据实体类查询
     *
     * @param userWindowDto
     * @return
     */
    List<UserWindowDto> queryListByBean(UserWindowDto userWindowDto);


    List<SysUserEntity> queryList(Map<String, Object> map);

    List<SysUserEntity> queryList(Object id);

    int queryTotal(Map<String, Object> map);

    int queryTotal();
}
