package com.unifig.organ.dao;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.organ.model.SysMenuEntity;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理
 *

 * @date 2018-10-24
 */
@TableName("ums_sys_menu")
public interface UmsSysMenuDao extends BaseMapper<SysMenuEntity> {

    /**
     * 根据父菜单，查询子菜单
     *
     * @param parentId 父菜单ID
     */
    List<SysMenuEntity> queryListParentId(Long parentId);

    /**
     * 获取不包含按钮的菜单列表
     */
    List<SysMenuEntity> queryNotButtonList();

    /**
     * 查询用户的权限列表
     */
    List<SysMenuEntity> queryUserList(Long userId);

    int save(SysMenuEntity t);

    void save(Map<String, Object> map);

    void saveBatch(List<SysMenuEntity> list);

    int deleteBatch(Object[] id);

    SysMenuEntity queryObject(Object id);

    List<SysMenuEntity> queryList(Map<String, Object> map);

    List<SysMenuEntity> queryList(Object id);

    int queryTotal(Map<String, Object> map);

    int queryTotal();
}
