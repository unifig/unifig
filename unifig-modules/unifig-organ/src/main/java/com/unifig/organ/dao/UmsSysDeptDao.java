package com.unifig.organ.dao;

import com.unifig.organ.dto.UserWindowDto;
import com.unifig.dao.BaseDao;
import com.unifig.organ.model.SysDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门管理
 *

 * @date 2018-10-24
 */
@Mapper
public interface UmsSysDeptDao extends BaseDao<SysDeptEntity> {

    /**
     * 查询子部门ID列表
     *
     * @param parentId 上级部门ID
     */
    List<Long> queryDetpIdList(Long parentId);


    /**
     * 根据实体条件查询
     *
     * @return
     */
    List<UserWindowDto> queryPageByDto(UserWindowDto userWindowDto);
}
