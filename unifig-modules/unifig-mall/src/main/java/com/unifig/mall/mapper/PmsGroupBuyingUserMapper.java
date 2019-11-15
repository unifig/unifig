package com.unifig.mall.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.mall.bean.vo.PmsGroupBuyingUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户参团记录 Mapper 接口
 * </p>
 *
 *
 * @since 2019-06-25
 */
public interface PmsGroupBuyingUserMapper extends BaseMapper<PmsGroupBuyingUser> {

    List<PmsGroupBuyingUserVo> selectListByUserId(Pagination page, @Param("type") Integer type, @Param("userId") String userId);
}
