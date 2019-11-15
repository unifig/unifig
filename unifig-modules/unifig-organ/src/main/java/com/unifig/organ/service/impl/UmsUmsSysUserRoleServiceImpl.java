package com.unifig.organ.service.impl;

import com.unifig.organ.dao.UmsSysUserRoleDao;
import com.unifig.organ.service.UmsSysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户与角色对应关系
 *

 * @date 2018-10-24
 */
@Service("sysUserRoleService")
public class UmsUmsSysUserRoleServiceImpl implements UmsSysUserRoleService {
    @Autowired
    private UmsSysUserRoleDao umsSysUserRoleDao;

    @Override
    public void saveOrUpdate(Long userId, List<Long> roleIdList) {
        if (roleIdList.size() == 0) {
            return;
        }

        //先删除用户与角色关系
        umsSysUserRoleDao.delete(userId);

        //保存用户与角色关系
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("roleIdList", roleIdList);
        umsSysUserRoleDao.save(map);
    }

    @Override
    public List<Long> queryRoleIdList(Long userId) {
        return umsSysUserRoleDao.queryRoleIdList(userId);
    }

    @Override
    public void delete(Long userId) {
        umsSysUserRoleDao.delete(userId);
    }
}
