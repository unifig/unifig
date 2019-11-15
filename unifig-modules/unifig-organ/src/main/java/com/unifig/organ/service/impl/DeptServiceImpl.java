package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.dao.DeptMapper;
import com.unifig.organ.dao.DeptRecordMapper;
import com.unifig.organ.dao.UserMapper;
import com.unifig.organ.domain.*;
import com.unifig.organ.service.DeptService;
import com.unifig.result.ResultData;
import com.unifig.utils.MD5Util;
import com.unifig.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

import static com.unifig.context.Constants.*;

/**
 * <p>
 * 部门管理 服务实现类
 * </p>
 *
 *
 * @since 2019-03-06
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

	@Autowired
	private DeptMapper deptMapper;

	@Autowired
	private DeptRecordMapper deptRecordMapper;

	@Autowired
	private UserMapper userMapper;

	/**
	 * Feign使用的根据部门ID获取部门信息
	 *
	 * @param id
	 * @return
	 */
	@Override
	public Dept findDeptById(Long id) {
		return deptMapper.selectById(id);
	}

	/**
	 * 根据机构id获取机构信息
	 *
	 * @param userCache
	 * @param id
	 * @return
	 */
	@Override
	public ResultData selectDeptById(UserCache userCache, Long id) {
		if (null == id) {
			String deptId = userCache.getDeptId();
			id = Long.parseLong(deptId);
		}
		Dept dept = deptMapper.selectById(id);
		return ResultData.result(true).setData(dept);
	}

	/**
	 * 新增机构信息
	 *
	 * @param userCache
	 * @param dept
	 * @return
	 */
	@Override
	@Transactional
	public ResultData createDept(UserCache userCache, Dept dept) {
		List<Dept> depts = deptMapper.selectList((Condition.create().eq("name", dept.getName())));
		if (null != depts && depts.size() > 0) {
			return ResultData.result(false).setMsg("创建机构失败,机构名称重复");
		}
		String userUsername = dept.getUserUsername();
		List<User> users = userMapper.selectList((Condition.create().eq("username", userUsername)));
		if (null != users && users.size() > 0) {
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("创建机构失败,管理员账号重复!");
		}
		Long parentId = dept.getParentId();
		Dept parentDept = deptMapper.selectById(parentId);
		String parentname = parentDept.getName();
		Integer deptLevel = parentDept.getDeptLevel();
		if (null != deptLevel && deptLevel >= ORGAN_DEPT_LEVEL) {
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("上级机构为最低等级不允许新增下级机构!");
		}
		dept.setDeptLevel(deptLevel + 1);
		//获取当前用户信息
		Long userId = Long.parseLong(userCache.getUserId());
		Date date = new Date();
		Dept deptParent = deptMapper.selectById(dept.getParentId());
		//上级机构名称
		dept.setParentName(deptParent.getName());
		//设置排序字段
		dept.setOrderNum(Integer.parseInt(String.valueOf(dept.getParentId())));
		//是否删除
		dept.setDelFlag(DEFAULT_VAULE_ZERO);
		//机构状态
		dept.setStatus(String.valueOf(DEFAULT_VAULE_ONE));
		//创建者
		dept.setCreateUserId(userId);
		//创建时间
		dept.setCreateTime(date);
		//入库返回机构ID
		deptMapper.insert(dept);
		Long deptId = dept.getDeptId();

		//新增账号
		User user = new User();
		//用户名去重
		List<User> usersName = userMapper.selectList(Condition.create().eq("username", userUsername));
		if (usersName.size() > 0) {
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("用户名重复,请核实!");
		}
		user.setUsername(userUsername);
		String phone = dept.getPhone();
		//手机号去重
		List<User> usersMobile = userMapper.selectList(Condition.create().eq("mobile", phone));
		if (usersMobile.size() > 0) {
			//回滚事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResultData.result(false).setMsg("手机号重复,请核实!");
		}
		//设置手机号
		user.setMobile(phone);
		String contacts = dept.getContacts();
		user.setHolderName(contacts);
		user.setAccountName(dept.getName() + "管理员账号");
		String defaultPassword = dept.getDefaultPassword();
		//sha256加密
		defaultPassword = MD5Util.getMD5(defaultPassword);
		user.setPassword(defaultPassword);
		//设置部门id
		user.setDeptId(deptId);
		//设置账户部门名称
		user.setDeptName(dept.getName());
		//用户入库
		userMapper.insert(user);
		Long adminId = user.getId();
		UserRole userRole = new UserRole();
		//设置管理员账户默认关联下级机构管理员角色
		userRole.setRoleId(Long.parseLong("2"));
		userRole.setUserId(adminId);
		userRole.insert();
		//管理员ID
		dept.setUserId(adminId);
		//更新部门信息
		deptMapper.updateById(dept);


		//创建机构记录信息
		DeptRecord deptRecord = new DeptRecord();
		//bean拷贝
		BeanUtils.copyProperties(dept, deptRecord);
		//设置上级机构名称
		deptRecord.setParentName(parentname);
		//操作状态
		deptRecord.setOperateStatus(String.valueOf(DEFAULT_VAULE_THREE));
		//机构操作记录入库
		deptRecordMapper.insert(deptRecord);
		return ResultData.result(true).setMsg("创建机构成功");
	}

	/**
	 * 停用启用机构
	 *
	 * @param userCache
	 * @param status
	 * @return
	 */
	@Override
	@Transactional
	public ResultData upadteDeptStatus(UserCache userCache, String ids, String status) {

		//获取当前用户信息
		Long userId = Long.parseLong(userCache.getUserId());
		Date date = new Date();
		if (StringUtils.isBlank(ids) && StringUtils.isBlank(status)) {
			return ResultData.result(false).setMsg("机构ID与状态不能为空");
		}
		if (!status.equals(String.valueOf(DEFAULT_VAULE_ZERO)) && !status.equals(String.valueOf(DEFAULT_VAULE_ONE))) {
			return ResultData.result(false).setMsg("请检查机构状态是否正确");
		}
		ids.replaceAll("，", ",");
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			Dept dept = deptMapper.selectById(id);
			//查询出该机构关联的所有用户
			List<User> users = userMapper.selectList((Condition.create().eq("dept_id", id)));
			if (null != users && users.size() > 0) {
				for (User user : users) {
					user.setStatus(Integer.parseInt(status));
					user.setUpdateUserId(userId);
					user.setUpdateTime(date);
					userMapper.updateById(user);
				}
			}
			dept.setStatus(status);
			dept.setUpdateUserId(userId);
			dept.setUpdateTime(date);
			deptMapper.updateById(dept);
			//创建机构记录信息
			DeptRecord deptRecord = new DeptRecord();
			//bean拷贝
			BeanUtils.copyProperties(dept, deptRecord);
			//设置上级机构名称
			Long parentId = dept.getParentId();
			deptRecord.setParentName(deptMapper.selectById(parentId).getName());
			//操作状态
			deptRecord.setOperateStatus(String.valueOf(status));
			//机构操作记录入库
			deptRecordMapper.insert(deptRecord);
		}
		return ResultData.result(true).setMsg(status.equals(DEFAULT_VAULE_ONE) ? "启用" : "停用" + "机构成功");
	}

	/**
	 * 重置管理员密码
	 *
	 * @param userCache
	 * @param id
	 * @param password
	 * @return
	 */
	@Override
	public ResultData upadteDeptAdminPassword(UserCache userCache, Long id, String password) {
		//获取当前用户信息
		Long userId = Long.parseLong(userCache.getUserId());
		Date date = new Date();
		if (null == id && StringUtils.isBlank(password)) {
			return ResultData.result(false).setMsg("机构ID与密码不能为空");
		}
		Dept dept = deptMapper.selectById(id);
		if (null == dept) {
			return ResultData.result(false).setMsg("机构ID错误请核实");
		}
		Long adminId = dept.getUserId();
		User user = userMapper.selectById(adminId);
		//sha256加密
		password = MD5Util.getMD5(password);
		user.setPassword(password);
		user.setUpdateUserId(userId);
		user.setUpdateTime(date);
		userMapper.updateById(user);
		dept.setUpdateUserId(userId);
		dept.setUpdateTime(date);
		deptMapper.updateById(dept);
		//创建机构记录信息
		DeptRecord deptRecord = new DeptRecord();
		//bean拷贝
		BeanUtils.copyProperties(dept, deptRecord);
		//设置上级机构名称
		Long parentId = dept.getParentId();
		deptRecord.setParentName(deptMapper.selectById(parentId).getName());
		//操作状态
		deptRecord.setOperateStatus(String.valueOf(DEFAULT_VAULE_TOW));
		//机构操作记录入库
		deptRecordMapper.insert(deptRecord);
		return ResultData.result(true).setMsg("修改机构管理员密码成功");
	}

	/**
	 * 分页获取机构信息列表
	 *
	 * @param userCache
	 * @param deptVo
	 * @return
	 */
	@Override
	public ResultData findeDeptListByDeptVo(UserCache userCache, DeptVo deptVo) {
		//获取当前用户信息
		String userDeptId = userCache.getDeptId();
		//创建条件构造器
		EntityWrapper<Dept> ew = new EntityWrapper<Dept>();
		ew.setEntity(new Dept());
		//部门名称(机构名称)
		String name = deptVo.getName();
		if (StringUtils.isNotBlank(name)) {
			ew.eq("name", name);
		}
		//机构状态(0:停用,1:正常)
		String status = deptVo.getStatus();
		if (StringUtils.isNotBlank(status)) {
			ew.eq("status", status);
		}
		//机构代码
		String organizationCode = deptVo.getOrganizationCode();
		if (StringUtils.isNotBlank(organizationCode)) {
			ew.eq("organization_code", organizationCode);
		}
		//当前机构id
		Long deptId = deptVo.getDeptId();
		if (null != deptId) {
			userDeptId = String.valueOf(deptId);
		}
		//查询出所有下级机构id
		List<Long> allSubDepts = findAllSubDept(Long.parseLong(userDeptId));
		if (null != allSubDepts && allSubDepts.size() > 0) {
			ew.in("dept_id", allSubDepts);
		} else {
			ew.eq("parent_id", userDeptId);
		}
		//当前页
		Integer pageNum = deptVo.getPageNum();
		if (null == pageNum) {
			pageNum = 1;
		}
		//页大小
		Integer pageSize = deptVo.getPageSize();
		if (null == pageSize) {
			pageSize = 10;
		}
		ew.orderBy(" parent_id asc ");
		Integer count = deptMapper.selectCount(ew);
		List<Dept> deptList = deptMapper.selectPage(new Page<Dept>(pageNum, pageSize), ew);
		return ResultData.result(true).setData(deptList).setCount(count);
	}

	/**
	 * 获取当前用户机构及下级机构列表(不传deptId默认查当前用户部门,传了查deptId部门)
	 *
	 * @param userCache
	 * @param deptId
	 * @param resultType
	 * @param user
	 * @return
	 */
	@Override
	public ResultData findeDeptListByDeptId(UserCache userCache, String deptId, String resultType, String user) {
		if (StringUtils.isBlank(deptId) && StringUtils.isBlank(resultType)) {
			deptId = "1";
		} else if (StringUtils.isBlank(deptId) && StringUtils.isNotBlank(resultType)) {
			deptId = userCache.getDeptId();
		}
		//返回树状数据
		Map<String, Object> mapResult = new HashMap<>();

		List<Map> maps = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		Dept dept = deptMapper.selectById(deptId);
		if (null == dept) {
			return ResultData.result(false).setMsg("机构ID错误请核实");
		}
		Long id = dept.getDeptId();
		String name = dept.getName();
		map.put("deptId", id);
		map.put("name", name);
		if (StringUtils.isNotBlank(user)) {
			String userUsername = dept.getUserUsername();
			map.put("account", userUsername);
			List<User> usersName = userMapper.selectList(Condition.create().eq("username", userUsername));
			User us = usersName.get(0);
			if (null != us) {
				map.put("password", us.getPassword());
			} else {
				map.put("password", dept.getDefaultPassword());
			}
		}
		List<Dept> deptsOne = deptMapper.selectList((Condition.create().eq("parent_id", deptId)));
		if (null != deptsOne && deptsOne.size() > 0) {
			List<Map> mapOnes = new ArrayList<>();
			//第一级循环
			for (Dept deptOne : deptsOne) {
				Map<String, Object> mapOne = new HashMap<>();
				Long deptIdOne = deptOne.getDeptId();
				String nameOne = deptOne.getName();
				mapOne.put("deptId", deptIdOne);
				mapOne.put("name", nameOne);
				if (StringUtils.isNotBlank(user)) {
					String userUsername = deptOne.getUserUsername();
					mapOne.put("account", userUsername);
					List<User> usersName = userMapper.selectList(Condition.create().eq("username", userUsername));
					User us = usersName.get(0);
					if (null != us) {
						mapOne.put("password", us.getPassword());
					} else {
						mapOne.put("password", deptOne.getDefaultPassword());
					}
				}
				List<Dept> deptsTow = deptMapper.selectList((Condition.create().eq("parent_id", deptIdOne)));
				if (null != deptsTow && deptsTow.size() > 0) {
					List<Map> mapTows = new ArrayList<>();
					//第二级循环
					for (Dept deptTow : deptsTow) {
						Map<String, Object> mapTow = new HashMap<>();
						Long deptIdTow = deptTow.getDeptId();
						String nameTow = deptTow.getName();
						mapTow.put("deptId", deptIdTow);
						mapTow.put("name", nameTow);
						if (StringUtils.isNotBlank(user)) {
							String userUsername = deptTow.getUserUsername();
							mapTow.put("account", userUsername);
							List<User> usersName = userMapper.selectList(Condition.create().eq("username", userUsername));
							User us = usersName.get(0);
							if (null != us) {
								mapTow.put("password", us.getPassword());
							} else {
								mapTow.put("password", deptTow.getDefaultPassword());
							}
						}
						List<Dept> deptsThree = deptMapper.selectList((Condition.create().eq("parent_id", deptIdTow)));
						if (null != deptsThree && deptsThree.size() > 0) {
							List<Map> mapThrees = new ArrayList<>();
							//第三级循环
							for (Dept deptThree : deptsThree) {
								Map<String, Object> mapThree = new HashMap<>();
								Long deptIdThree = deptThree.getDeptId();
								String nameThree = deptThree.getName();
								mapThree.put("deptId", deptIdThree);
								mapThree.put("name", nameThree);
								if (StringUtils.isNotBlank(user)) {
									String userUsername = deptThree.getUserUsername();
									mapThree.put("account", userUsername);
									List<User> usersName = userMapper.selectList(Condition.create().eq("username", userUsername));
									User us = usersName.get(0);
									if (null != us) {
										mapThree.put("password", us.getPassword());
									} else {
										mapThree.put("password", deptThree.getDefaultPassword());
									}
								}
								mapThrees.add(mapThree);
							}
							mapTow.put("children", mapThrees);
						}
						mapTows.add(mapTow);
					}
					mapOne.put("children", mapTows);
				}
				mapOnes.add(mapOne);
			}
			map.put("children", mapOnes);
		}
		maps.add(map);
		mapResult.put("options", maps);
		return ResultData.result(true).setData(mapResult);
	}

	public List<Long> findAllSubDept(Long deptId) {
		List<Long> depts = new ArrayList<Long>();
		List<Dept> deptsOne = deptMapper.selectList((Condition.create().eq("parent_id", deptId)));
		if (null != deptsOne && deptsOne.size() > 0) {
			//第一级循环
			for (Dept deptOne : deptsOne) {
				Long deptIdOne = deptOne.getDeptId();
				depts.add(deptIdOne);
				List<Dept> deptsTow = deptMapper.selectList((Condition.create().eq("parent_id", deptIdOne)));
				if (null != deptsOne && deptsOne.size() > 0) {
					//第二级循环
					for (Dept deptTow : deptsTow) {
						Long deptIdTow = deptTow.getDeptId();
						depts.add(deptIdTow);
						List<Dept> deptsThree = deptMapper.selectList((Condition.create().eq("parent_id", deptIdTow)));
						if (null != deptsOne && deptsOne.size() > 0) {
							//第三级循环
							for (Dept deptThree : deptsThree) {
								Long deptIdThree = deptThree.getDeptId();
								depts.add(deptIdThree);
							}
						}
					}
				}
			}
		}
		return depts;
	}


}
