package com.unifig.organ.service.impl;

import com.unifig.organ.dao.UmsSysDeptDao;
import com.unifig.organ.dto.UserWindowDto;
import com.unifig.organ.service.UmsSysDeptService;
import com.unifig.organ.dto.PageHelper;
import com.unifig.organ.model.SysDeptEntity;
import com.unifig.page.Page;
import com.unifig.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("sysDeptService")
public class UmsUmsSysDeptServiceImpl implements UmsSysDeptService {
    @Autowired
    private UmsSysDeptDao umsSysDeptDao;

    @Override
    public SysDeptEntity queryObject(Long deptId) {
        return umsSysDeptDao.queryObject(deptId);
    }

    @Override
    public List<SysDeptEntity> queryList(Map<String, Object> map) {
        return umsSysDeptDao.queryList(map);
    }

    @Override
    public void save(SysDeptEntity sysDept) {
        umsSysDeptDao.save(sysDept);
    }

    @Override
    public void update(SysDeptEntity sysDept) {
        umsSysDeptDao.update(sysDept);
    }

    @Override
    public void delete(Long deptId) {
        umsSysDeptDao.delete(deptId);
    }

    @Override
    public List<Long> queryDetpIdList(Long parentId) {
        return umsSysDeptDao.queryDetpIdList(parentId);
    }

    @Override
    public String getSubDeptIdList(Long deptId) {
        //部门及子部门ID列表
        List<Long> deptIdList = new ArrayList<>();

        //获取子部门ID
        List<Long> subIdList = queryDetpIdList(deptId);
        getDeptTreeList(subIdList, deptIdList);

        //添加本部门
        deptIdList.add(deptId);

        String deptFilter = StringUtils.join(deptIdList, ",");
        return deptFilter;
    }

    /**
     * 递归
     */
    public void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList) {
        for (Long deptId : subIdList) {
            List<Long> list = queryDetpIdList(deptId);
            if (list.size() > 0) {
                getDeptTreeList(list, deptIdList);
            }

            deptIdList.add(deptId);
        }
    }

    @Override
    public Page<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto, int pageNum) {
        PageHelper.startPage(pageNum, Constant.pageSize);
        umsSysDeptDao.queryPageByDto(userWindowDto);
        return PageHelper.endPage();
    }
}
