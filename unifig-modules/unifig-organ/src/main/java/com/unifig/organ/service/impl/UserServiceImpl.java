package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.dao.UmsSysUserRoleDao;
import com.unifig.organ.dao.UserMapper;
import com.unifig.organ.dao.UserRoleMapper;
import com.unifig.organ.domain.*;
import com.unifig.organ.mapper.RoleMapper;
import com.unifig.organ.service.DeptService;
import com.unifig.organ.service.UserService;
import com.unifig.result.ResultData;
import com.unifig.utils.MD5Util;
import com.unifig.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.unifig.context.Constants.DEFAULT_VAULE_ZERO;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UmsSysUserRoleDao umsSysUserRoleDao;

	@Autowired
	private DeptService deptService;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 创建账号
	 *
	 * @param userCache
	 * @param user
	 * @return
	 */
	@Override
	@Transactional
	public ResultData createUser(UserCache userCache, User user) {
		//获取当前用户信息
		String userDeptId = userCache.getDeptId();
		Date date = new Date();
		//设置手机号
		String username = user.getUsername();
		String mobile = user.getMobile();
		String password = user.getPassword();
		//用户名去重
		List<User> usersName = userMapper.selectList(Condition.create().eq("username", username));
		if (usersName.size() > 0) {
			return ResultData.result(false).setMsg("用户名重复,请核实!");
		}
		//手机号去重
		List<User> usersMobile = userMapper.selectList(Condition.create().eq("mobile", mobile));
		if (usersMobile.size() > 0) {
			return ResultData.result(false).setMsg("手机号重复,请核实!");
		}
		//sha256加密
		password = MD5Util.getMD5(password);
		user.setPassword(password);
		user.setCreateUserId(Long.parseLong(userDeptId));
		user.setCreateTime(date);
		try {
			userMapper.insert(user);
		} catch (Exception e) {
			e.printStackTrace();
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("用户名重复,请核实!");
		}
		//保存角色关联关系
		Long id = user.getId();
		String roleId = user.getRoleId();
		if (StringUtils.isBlank(roleId)) {
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("角色id不能为空");
		}
		roleId = roleId.replaceAll("，", ",");
		String[] split = roleId.split(",");
		for (String s : split) {
			if (StringUtils.isBlank(s)) {
				//回滚事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ResultData.result(false).setMsg("角色id参数错误!");
			}
			UserRole userRole = new UserRole();
			userRole.setRoleId(Long.parseLong(s));
			userRole.setUserId(id);
			userRole.insert();
		}
		return ResultData.result(true).setMsg("创建账号成功");
	}

	/**
	 * 更新账号
	 *
	 * @param userCache
	 * @param user
	 * @return
	 */
	@Override
	@Transactional
	public ResultData updateUser(UserCache userCache, User user) {
		//获取当前用户信息
		String userDeptId = userCache.getDeptId();
		Date date = new Date();
		String password = user.getPassword();
		//sha256加密
		password = MD5Util.getMD5(password);
		user.setPassword(password);
		user.setUpdateUserId(Long.parseLong(userDeptId));
		user.setUpdateTime(date);
		Long userId = user.getId();
		try {
			userMapper.updateById(user);
		} catch (Exception e) {
			e.printStackTrace();
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("用户名重复,请核实!");
		}
		String roleId = user.getRoleId();
		if (StringUtils.isNotBlank(roleId)) {
			userRoleMapper.delete(Condition.create().eq("user_id", userId));
			roleId = roleId.replaceAll("，", ",");
			String[] split = roleId.split(",");
			for (String s : split) {
				if (StringUtils.isBlank(s)) {
					//回滚事务
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return ResultData.result(false).setMsg("角色id参数错误!");
				}
				UserRole userRole = new UserRole();
				userRole.setRoleId(Long.parseLong(s));
				userRole.setUserId(userId);
				userRole.insert();
			}
		}
		return ResultData.result(true).setMsg("更新账号成功");
	}

	/**
	 * 删除账号信息
	 *
	 * @param userCache
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public ResultData deleteUser(UserCache userCache, Long id) {
		User user = userMapper.selectById(id);
		List<UserRole> userRole = userRoleMapper.selectList(Condition.create().eq("user_id", id));
		for (UserRole role : userRole) {
			Long roleId = role.getRoleId();
			if (Integer.parseInt(String.valueOf(roleId)) <= 2) {
				//回滚事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ResultData.result(false).setMsg("机构管理员账户,不允许删除!");
			}
		}
		//获取当前用户信息
		String userDeptId = userCache.getDeptId();
		Date date = new Date();
		user.setUpdateUserId(Long.parseLong(userDeptId));
		user.setUpdateTime(date);
		user.setStatus(DEFAULT_VAULE_ZERO);
		user.setIsDelete(String.valueOf(DEFAULT_VAULE_ZERO));
		userRoleMapper.delete(Condition.create().eq("user_id", id));
		userMapper.updateById(user);
		return ResultData.result(true).setMsg("删除账号成功");
	}

	/**
	 * 分页获取账号信息列表
	 *
	 * @param userCache
	 * @param userVo
	 * @return
	 */
	@Override
	public ResultData findeUserdListByUsertVo(UserCache userCache, UserVo userVo) {
		//获取当前用户信息
		String userDeptId = userCache.getDeptId();
		String userId = userCache.getUserId();
		List<Long> allSubDept = deptService.findAllSubDept(Long.parseLong(userDeptId));
		if (null != allSubDept && allSubDept.size() > 0) {
			allSubDept.add(Long.parseLong(userDeptId));
		} else {
			allSubDept = new ArrayList<Long>();
			allSubDept.add(Long.parseLong(userDeptId));
		}
		//创建条件构造器
		EntityWrapper<User> ew = new EntityWrapper<User>();
		ew.setEntity(new User());
		if (!"1".equals(userId)){
			ew.notIn("id","1");
		}
		ew.in("dept_id", allSubDept);
		ew.eq("is_delete", "1");
		//查询字段S
		String searchField = userVo.getSearchField();
		if (StringUtils.isNotBlank(searchField)) {
			ew.and(" (username='" + searchField + "'or dept_name='" + searchField + "' or holder_name = '" + searchField + "') ");
		}
		//根据手机号查询
		if (StringUtils.isNotBlank(userVo.getMobile())) {
			ew.like("mobile", userVo.getMobile());
		}
		//当前页
		Integer pageNum = userVo.getPageNum();
		if (null == pageNum) {
			pageNum = 1;
		}
		//页大小
		Integer pageSize = userVo.getPageSize();
		if (null == pageSize) {
			pageSize = 10;
		}
		ew.orderBy(" dept_id asc ");
		Integer count = userMapper.selectCount(ew);
		List<User> userList = userMapper.selectPage(new Page<User>(pageNum, pageSize), ew);
		for (User user : userList) {
			Long deptId = user.getDeptId();
			Dept deptById = deptService.findDeptById(deptId);
			String name = deptById.getName();
			user.setDeptName(name);
			Long id = user.getId();
			List<Long> longs = umsSysUserRoleDao.queryRoleIdList(id);
			String join = StringUtils.join(longs.toArray(), ",");
			user.setRoleId(join);
			ArrayList<String> roleNames = new ArrayList<>();
			for (Long roleId : longs) {
				Role role = roleMapper.selectById(roleId);
				if (null != role) {
					String roleName = role.getRoleName();
					roleNames.add(roleName);
				}
			}
			String joinName = StringUtils.join(roleNames.toArray(), ",");
			user.setRoleName(joinName);
		}
		return ResultData.result(true).setData(userList).setCount(count);
	}

	@Override
	public int updateUserShopId(String uuid, String shopId) {
		User user = userMapper.selectById(uuid);
		if (user != null) {
			user.setShopId(shopId);
			user.updateById();
			return 1;
		}
		return 0;
	}

	@Override
	public int updateUserShopId(String uuid, String shopId, String openId) {
		User user = userMapper.selectById(uuid);
		if (user != null) {
			user.setShopId(shopId);
			user.setOpenId(openId);
			user.updateById();
			return 1;
		}
		return 0;
	}
}
