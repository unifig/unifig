package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.organ.dao.UmsSysUserDao;
import com.unifig.organ.dto.PageHelper;
import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysUserEntity;
import com.unifig.organ.service.UmsSysRoleService;
import com.unifig.organ.service.UmsSysUserRoleService;
import com.unifig.organ.service.UmsSysUserService;
import com.unifig.page.Page;
import com.unifig.utils.Constant;
import com.unifig.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *

 * @date 2018-10-24
 */
@Service("sysUserService")
public class UmsUmsSysUserServiceImpl extends ServiceImpl<UmsSysUserDao, SysUserEntity> implements UmsSysUserService {

    @Autowired
    private UmsSysUserDao umsSysUserDao;
    @Autowired
    private UmsSysUserRoleService sysUserRoleService;
    @Autowired
    private UmsSysRoleService sysRoleService;

    @Override
    public List<String> queryAllPerms(Long userId) {
        return umsSysUserDao.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return umsSysUserDao.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return umsSysUserDao.queryByUserName(username);
    }

    @Override
    public SysUserEntity queryObject(Long userId) {
        return umsSysUserDao.selectById(userId);
    }

    @Override
    public List<SysUserEntity> queryList(Map<String, Object> map) {
        return umsSysUserDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysUserDao.queryTotal(map);
    }

    @Override
    @Transactional
    public void save(SysUserEntity user) {
        user.setCreateTime(new Date());
        //sha256加密
        user.setPassword(MD5Util.getMD5(Constant.DEFAULT_PASS_WORD));
        umsSysUserDao.insert(user);

        //检查角色是否越权
        checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    @Transactional
    public void update(SysUserEntity user) {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(MD5Util.getMD5(Constant.DEFAULT_PASS_WORD));
        } else {
            user.setPassword(MD5Util.getMD5(user.getPassword()));
        }
        umsSysUserDao.updateById(user);

        //检查角色是否越权
        checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    public int updatePassword(Long userId, String password, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("password", password);
        map.put("newPassword", newPassword);
        return umsSysUserDao.updatePassword(map);
    }

    /**
     * 检查角色是否越权
     */
    private void checkRole(SysUserEntity user) {
        //如果不是超级管理员，则需要判断用户的角色是否自己创建
        if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
            return;
        }
        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());
    }


    @Override
    public Page<UserWindowDto> findPage(UserWindowDto userWindowDto, int pageNum) {
        PageHelper.startPage(pageNum, Constant.pageSize);
        umsSysUserDao.queryListByBean(userWindowDto);
        return PageHelper.endPage();
    }

    @Override
    public SysUserEntity login(String username, String password) {
        return umsSysUserDao.queryByUserName(username);
    }

    @Override
    public void deleteBatch(Long[] userIds) {
        for (Long userId : userIds) {
            umsSysUserDao.deleteById(userId);
        }
    }

}
