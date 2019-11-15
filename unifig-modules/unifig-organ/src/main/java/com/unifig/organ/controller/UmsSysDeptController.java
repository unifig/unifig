package com.unifig.organ.controller;

import com.unifig.organ.dto.R;
import com.unifig.organ.model.SysDeptEntity;
import com.unifig.organ.service.UmsSysDeptService;
import com.unifig.result.ResultData;
import com.unifig.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门管理Controller
 *
 * @author liepngjun
 *
 * @date 2018-10-24
 */
@RestController
@RequestMapping("/sys/dept")
@ApiIgnore
public class UmsSysDeptController extends AbstractController {
    @Autowired
    private UmsSysDeptService sysDeptService;

    /**
     * 部门列表
     *
     * @return R
     */
    @RequestMapping("/list")
    public ResultData list() {
        Map<String, Object> map = new HashMap<>();
        //如果不是超级管理员，则只能查询本部门及子部门数据
        if (getUserId() != Constant.SUPER_ADMIN) {
            map.put("deptFilter", sysDeptService.getSubDeptIdList(getDeptId()));
        }
        List<SysDeptEntity> deptList = sysDeptService.queryList(map);
        // return R.ok().put("list", deptList);
        return ResultData.result(true).setMsg("true").setData(deptList);

    }

    /**
     * 选择部门(添加、修改菜单)
     *
     * @return R
     */
    @RequestMapping("/select")
    public R select() {
        Map<String, Object> map = new HashMap<>();
        //如果不是超级管理员，则只能查询本部门及子部门数据
        if (getUserId() != Constant.SUPER_ADMIN) {
            map.put("deptFilter", sysDeptService.getSubDeptIdList(getDeptId()));
        }
        List<SysDeptEntity> deptList = sysDeptService.queryList(map);

        //添加一级部门
        if (getUserId() == Constant.SUPER_ADMIN) {
            SysDeptEntity root = new SysDeptEntity();
            root.setDeptId(0L);
            root.setName("一级部门");
            root.setParentId(-1L);
            root.setOpen(true);
            deptList.add(root);
        }

        return R.ok().put("deptList", deptList);
    }

    /**
     * 获取用户部门Id(管理员则为0)
     *
     * @return
     */
    @RequestMapping("/info")
    public R info() {
        long deptId = 0;
        if (getUserId() != Constant.SUPER_ADMIN) {
            SysDeptEntity dept = sysDeptService.queryObject(getDeptId());
            deptId = dept.getParentId();
        }

        return R.ok().put("deptId", deptId);
    }

    /**
     * 根据主键获取部门信息
     *
     * @param deptId 主键
     * @return R
     */
    @RequestMapping("/info/{deptId}")
    public R info(@PathVariable("deptId") Long deptId) {
        SysDeptEntity dept = sysDeptService.queryObject(deptId);

        return R.ok().put("dept", dept);
    }

    /**
     * 新增部门
     *
     * @param dept 部门
     * @return R
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysDeptEntity dept) {
        sysDeptService.save(dept);

        return R.ok();
    }

    /**
     * 修改部门
     *
     * @param dept 部门
     * @return R
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysDeptEntity dept) {
        sysDeptService.update(dept);

        return R.ok();
    }

    /**
     * 删除部门
     *
     * @param deptId 主键
     * @return R
     */
    @RequestMapping("/delete")
    public R delete(long deptId) {
        //判断是否有子部门
        List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
        if (deptList.size() > 0) {
            return R.error("请先删除子部门");
        }

        sysDeptService.delete(deptId);

        return R.ok();
    }

}
