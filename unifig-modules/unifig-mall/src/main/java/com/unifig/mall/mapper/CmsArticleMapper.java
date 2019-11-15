package com.unifig.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.mall.bean.model.CmsArticle;
import com.unifig.mall.bean.vo.HomeCmsArticleVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 *
 * @since 2019-01-22
 */
public interface CmsArticleMapper extends BaseMapper<CmsArticle> {

    /**
     * 查询首页列表
     *
     * @return
     */
    List<HomeCmsArticleVo> selectHomeList(Map<String, Object> params);
}
