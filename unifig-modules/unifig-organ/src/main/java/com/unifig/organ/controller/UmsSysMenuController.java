package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.organ.dto.PageUtils;
import com.unifig.organ.dto.R;
import com.unifig.organ.model.SysMenuEntity;
import com.unifig.organ.service.UmsSysMenuService;
import com.unifig.result.ResultData;
import com.unifig.utils.Constant;
import com.unifig.utils.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统菜单
 *

 * @date 2018-10-24
 */
@RestController
@RequestMapping("/sys/menu")
@ApiIgnore
public class UmsSysMenuController extends AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UmsSysMenuService sysMenuService;

    /**
     * 所有菜单列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysMenuEntity> menuList = sysMenuService.queryList(query);
        int total = sysMenuService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(menuList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 所有菜单列表
     */
    @RequestMapping("/queryAll")
    public R queryAll(@RequestParam Map<String, Object> params) {
        //查询列表数据
        List<SysMenuEntity> menuList = sysMenuService.queryList(params);
        return R.ok().put("list", menuList);
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @RequestMapping("/select")
    public R select() {
        //查询列表数据
        List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();

        //添加顶级菜单
        SysMenuEntity root = new SysMenuEntity();
        root.setMenuId(0L);
        root.setName("一级菜单");
        root.setParentId(-1L);
        root.setOpen(true);
        menuList.add(root);

        return R.ok().put("menuList", menuList);
    }

    /**
     * 角色授权菜单
     */
    @RequestMapping("/perms")
    public ResultData perms() {
        //查询列表数据
        List<SysMenuEntity> menuList = null;

        //只有超级管理员，才能查看所有管理员列表
        if (getUserId() == Constant.SUPER_ADMIN) {
            menuList = sysMenuService.queryList(new HashMap<String, Object>());
        } else {
            menuList = sysMenuService.queryUserList(getUserId());
        }

        return ResultData.result(true).setData(menuList);
    }

    /**
     * 菜单信息
     */
    @RequestMapping("/info/{menuId}")
    public R info(@PathVariable("menuId") Long menuId) {
        SysMenuEntity menu = sysMenuService.queryObject(menuId);
        return R.ok().put("menu", menu);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public ResultData save(@RequestBody SysMenuEntity menu) {
        //数据校验
        try {
            verifyForm(menu);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultData.result(false);
        }

        sysMenuService.save(menu);

        return ResultData.result(true);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysMenuEntity menu) {
        //数据校验
        try {
            verifyForm(menu);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }

        sysMenuService.update(menu);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(String menuIds) {
        String[] split = menuIds.split(",");
        for (String s : split) {
            Long menuId = Long.parseLong(s);
            if (menuId.longValue() <= 30) {
                return R.error("系统菜单，不能删除");
            }
        }

        sysMenuService.deleteBatch(menuIds);

        return R.ok();
    }

    /**
     * 用户菜单列表
     */
    @RequestMapping("/user")
    public R user(String userId) {
        List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(Long.valueOf(userId));


        return R.ok().put("menuList", menuList);
    }


    /**
     * 用户菜单列表
     */
    @RequestMapping("/list/role")
    public ResultData listRole(@CurrentUser UserCache userCache) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(Long.valueOf(userCache.getUserId()));
//		List<MenuVo> menuVoList = new ArrayList<MenuVo>();
//		for (SysMenuEntity sysMenuEntity : menuList) {
//			MenuVo menuVo = new MenuVo();
//			menuVo.setMenuId(sysMenuEntity.getMenuId());
//			menuVo.setName(sysMenuEntity.getName());
//			menuVo.setParentId(sysMenuEntity.getParentId());
//			menuVo.setParentName(sysMenuEntity.getParentName());
//			menuVo.setPerms(sysMenuEntity.getPerms());
//			menuVo.setChildren(sysMenuEntity.getChildren());
//			menuVoList.add(menuVo);
//		}
        //生成权限字符串
        StringBuffer role = new StringBuffer();
        for (SysMenuEntity sysMenuEntity : menuList) {
            if (role.length() == 0) {
                role.append(sysMenuEntity.getPerms());
                continue;
            }
            role.append(",").append(sysMenuEntity.getPerms());
            List<SysMenuEntity> children = sysMenuEntity.getList();
            if (children == null) continue;
            for (SysMenuEntity child : children) {
                role.append(",").append(child.getPerms());
                List<SysMenuEntity> childrenC = child.getList();
                if (childrenC == null) continue;
                for (SysMenuEntity menuEntity : childrenC) {
                    role.append(",").append(menuEntity.getPerms());
                }
            }
        }
        result.put("menu", menuList);
        result.put("role", role.toString());
        return ResultData.result(true).setData(result);
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenuEntity menu) throws Exception {


        //上级菜单类型
        int parentType = Constant.MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenuEntity parentMenu = sysMenuService.queryByPareentId(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == Constant.MenuType.CATALOG.getValue() ||
                menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (parentType != Constant.MenuType.CATALOG.getValue()) {
                throw new Exception("上级菜单只能为目录类型");
            }
            return;
        }

        //按钮
        if (menu.getType() == Constant.MenuType.BUTTON.getValue()) {
            if (parentType != Constant.MenuType.MENU.getValue()) {
                throw new Exception("上级菜单只能为菜单类型");
            }
            return;
        }
    }
}
