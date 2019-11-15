package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.unifig.organ.dao.UmsSysMenuDao;
import com.unifig.organ.domain.Menu;
import com.unifig.organ.mapper.MenuMapper;
import com.unifig.organ.mapper.RoleMenuMapper;
import com.unifig.organ.model.SysMenuEntity;
import com.unifig.organ.service.UmsSysMenuService;
import com.unifig.organ.service.UmsSysRoleMenuService;
import com.unifig.organ.service.UmsSysUserService;
import com.unifig.utils.Constant.MenuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("sysMenuService")
public class UmsUmsSysMenuServiceImpl implements UmsSysMenuService {
	@Autowired
	private UmsSysMenuDao umsSysMenuDao;
	@Autowired
	private UmsSysUserService sysUserService;
	@Autowired
	private UmsSysRoleMenuService sysRoleMenuService;

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Override
	public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
		List<SysMenuEntity> menuList = umsSysMenuDao.queryListParentId(parentId);
		if (menuIdList == null) {
			return menuList;
		}

		List<SysMenuEntity> userMenuList = new ArrayList<>();
		for (SysMenuEntity menu : menuList) {
			if (menuIdList.contains(menu.getMenuId())) {
				userMenuList.add(menu);
			}
		}
		return userMenuList;
	}

	@Override
	public List<SysMenuEntity> queryNotButtonList() {
		return umsSysMenuDao.queryNotButtonList();
	}

	@Override
	public List<SysMenuEntity> getUserMenuList(Long userId) {
		//系统管理员，拥有最高权限
		if (userId == 1) {
			return getAllMenuList(null);
		}

		//用户菜单列表
		List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
		return getAllMenuList(menuIdList);
	}

	@Override
	public SysMenuEntity queryObject(Long menuId) {
		SysMenuEntity sysMenuEntity = umsSysMenuDao.selectById(menuId);
		Menu menu = menuMapper.selectById(sysMenuEntity.getParentId());
		if (menu != null) {
			sysMenuEntity.setParentName(menu.getName());
		}
		return sysMenuEntity;
	}

	@Override
	public SysMenuEntity queryByPareentId(Long parentId) {
		SysMenuEntity sysMenuEntity = umsSysMenuDao.selectById(parentId);
		return sysMenuEntity;
	}

	@Override
	public List<SysMenuEntity> queryList(Map<String, Object> map) {
		return umsSysMenuDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return umsSysMenuDao.queryTotal(map);
	}

	@Override
	public void save(SysMenuEntity menu) {
		umsSysMenuDao.insert(menu);
	}

	@Override
	public void update(SysMenuEntity menu) {
		umsSysMenuDao.updateById(menu);
	}

	@Override
	@Transactional
	public void deleteBatch(String menuIds) {
		menuMapper.delete(Condition.create().in("menu_id", menuIds));
		roleMenuMapper.delete(Condition.create().in("menu_id", menuIds));
		//umsSysMenuDao.deleteBatch(menuIds);
	}

	@Override
	public List<SysMenuEntity> queryUserList(Long userId) {
		return umsSysMenuDao.queryUserList(userId);
	}

	/**
	 * 获取所有菜单列表
	 */
	private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList) {
		//查询根菜单列表
		List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);
		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);

		return menuList;
	}

	/**
	 * 递归
	 */
	private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
		List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

		for (SysMenuEntity entity : menuList) {
			if (entity.getType() == MenuType.CATALOG.getValue()) {//目录
				entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}
			subMenuList.add(entity);
		}

		return subMenuList;
	}
}
