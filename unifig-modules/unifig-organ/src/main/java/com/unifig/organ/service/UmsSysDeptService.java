package com.unifig.organ.service;

import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.model.SysDeptEntity;
import com.unifig.page.Page;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *

 * @date 2018-10-24
 */
public interface UmsSysDeptService {

    SysDeptEntity queryObject(Long deptId);

    List<SysDeptEntity> queryList(Map<String, Object> map);

    void save(SysDeptEntity sysDept);

    void update(SysDeptEntity sysDept);

    void delete(Long deptId);

    /**
     * 查询子部门ID列表
     *
     * @param parentId 上级部门ID
     */
    List<Long> queryDetpIdList(Long parentId);

    /**
     * 获取子部门ID(包含本部门ID)，用于数据过滤
     */
    String getSubDeptIdList(Long deptId);

    /**
     * 分页查询组织审批选择范围
     *
     * @return
     */
    Page<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto, int pageNum);
}
