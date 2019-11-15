package com.unifig.organ.service;

import com.unifig.organ.model.SysMenuEntity;

import java.util.List;
import java.util.Map;


/**
 * 菜单管理
 *

 * @date 2018-10-24
 */
public interface UmsSysMenuService {

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId   父菜单ID
     * @param menuIdList 用户菜单ID
     */
    List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList);

    /**
     * 获取不包含按钮的菜单列表
     */
    List<SysMenuEntity> queryNotButtonList();

    /**
     * 获取用户菜单列表
     */
    List<SysMenuEntity> getUserMenuList(Long userId);


    /**
     * 查询菜单
     */
    SysMenuEntity queryObject(Long menuId);

    /**
     * 查询菜单列表
     */
    List<SysMenuEntity> queryList(Map<String, Object> map);

    /**
     * 查询总数
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 保存菜单
     */
    void save(SysMenuEntity menu);

    /**
     * 修改
     */
    void update(SysMenuEntity menu);

    /**
     * 删除
	 * @param menuIds
	 */
    void deleteBatch(String menuIds);

    /**
     * 查询用户的权限列表
     */
    List<SysMenuEntity> queryUserList(Long userId);

    /**
     * 根据父级id查询
     * @param parentId
     * @return
     */
    SysMenuEntity queryByPareentId(Long parentId);
}
