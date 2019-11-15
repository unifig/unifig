package com.unifig.organ.service.impl;

import com.unifig.organ.domain.UserRole;
import com.unifig.organ.dao.UserRoleMapper;
import com.unifig.organ.service.UserRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户与角色对应关系 服务实现类
 * </p>
 *
 *
 * @since 2019-03-28
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
