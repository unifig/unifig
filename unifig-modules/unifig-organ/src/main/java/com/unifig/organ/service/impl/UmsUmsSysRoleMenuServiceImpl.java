package com.unifig.organ.service.impl;

import com.unifig.organ.dao.UmsSysRoleMenuDao;
import com.unifig.organ.service.UmsSysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 角色与菜单对应关系
 *

 * @date 2018-10-24
 */
@Service("sysRoleMenuService")
public class UmsUmsSysRoleMenuServiceImpl implements UmsSysRoleMenuService {
    @Autowired
    private UmsSysRoleMenuDao umsSysRoleMenuDao;

    @Override
    @Transactional
    public void saveOrUpdate(Long roleId, List<Long> menuIdList) {
        if (menuIdList.size() == 0) {
            return;
        }
        //先删除角色与菜单关系
        umsSysRoleMenuDao.delete(roleId);

        //保存角色与菜单关系
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("menuIdList", menuIdList);
        umsSysRoleMenuDao.save(map);
    }

    @Override
    public List<Long> queryMenuIdList(Long roleId) {
        return umsSysRoleMenuDao.queryMenuIdList(roleId);
    }

}
