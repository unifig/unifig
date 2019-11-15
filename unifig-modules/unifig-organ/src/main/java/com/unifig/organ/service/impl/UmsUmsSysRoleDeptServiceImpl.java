package com.unifig.organ.service.impl;

import com.unifig.organ.dao.UmsSysRoleDeptDao;
import com.unifig.organ.service.UmsSysRoleDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 角色与部门对应关系
 *

 * @date 2018-10-24
 */
@Service("sysRoleDeptService")
public class UmsUmsSysRoleDeptServiceImpl implements UmsSysRoleDeptService {
    @Autowired
    private UmsSysRoleDeptDao umsSysRoleDeptDao;

    @Override
    @Transactional
    public void saveOrUpdate(Long roleId, List<Long> deptIdList) {
        //先删除角色与菜单关系
        umsSysRoleDeptDao.delete(roleId);

        if (deptIdList.size() == 0) {
            return;
        }

        //保存角色与菜单关系
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("deptIdList", deptIdList);
        umsSysRoleDeptDao.save(map);
    }

    @Override
    public List<Long> queryDeptIdList(Long roleId) {
        return umsSysRoleDeptDao.queryDeptIdList(roleId);
    }

    @Override
    public List<Long> queryDeptIdListByUserId(Long userId) {
        return umsSysRoleDeptDao.queryDeptIdListByUserId(userId);
    }
}
