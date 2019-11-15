package com.unifig.mall.service;

import com.unifig.mall.bean.dto.PmsProductCategoryParam;
import com.unifig.mall.bean.dto.PmsProductCategoryWithChildrenItem;
import com.unifig.mall.bean.model.PmsProductCategory;
import com.unifig.mall.bean.vo.HomePmsProductCategoryVo;
import com.unifig.mall.bean.vo.PmsProductCategoryVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 产品分类Service
 *    on 2018/4/26.
 */
public interface PmsProductCategoryService {
    @Transactional
    int create(PmsProductCategoryParam pmsProductCategoryParam);

    @Transactional
    int update(Long id, PmsProductCategoryParam pmsProductCategoryParam);

    List<PmsProductCategory> getList(Long parentId, Integer pageSize, Integer pageNum);

    int delete(Long id);

    PmsProductCategory getItem(Long id);

    int updateNavStatus(List<Long> ids, Integer navStatus);

    int updateShowStatus(List<Long> ids, Integer showStatus);

    List<PmsProductCategoryWithChildrenItem> listWithChildren();

    List<PmsProductCategoryVo> getClientList(Long parentId);

    List<HomePmsProductCategoryVo> selectHomeList();
}
