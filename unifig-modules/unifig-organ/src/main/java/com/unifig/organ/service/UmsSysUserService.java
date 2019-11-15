package com.unifig.organ.service;


import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysUserEntity;
import com.unifig.page.Page;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *

 * @date 2018-10-24
 */
public interface UmsSysUserService {

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
     * 根据用户ID，查询用户
     *
     * @param userId
     * @return
     */
    SysUserEntity queryObject(Long userId);

    /**
     * 查询用户列表
     */
    List<SysUserEntity> queryList(Map<String, Object> map);

    /**
     * 查询总数
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 保存用户
     */
    void save(SysUserEntity user);

    /**
     * 修改用户
     */
    void update(SysUserEntity user);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param password    原密码
     * @param newPassword 新密码
     */
    int updatePassword(Long userId, String password, String newPassword);

    /**
     * 根据条件分页查询
     *
     * @param userEntity
     * @param pageNum
     * @return
     */
    Page<UserWindowDto> findPage(UserWindowDto userEntity, int pageNum);

    /**
     * 验证用户名密码
     *
     * @param username
     * @param password
     * @return
     */
    SysUserEntity login(String username, String password);

    void deleteBatch(Long[] userIds);
}
