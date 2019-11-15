package com.unifig.mall.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoList;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品团购子表 Mapper 接口
 * </p>
 *
 *
 * @since 2019-01-23
 */
public interface PmsGroupBuyingInfoMapper extends BaseMapper<PmsGroupBuyingInfo> {

    List<PmsGroupBuyingInfoList> selectByList(Pagination page, @Param("groupBuyingId") String groupBuyingId, @Param("status") Integer status);

    List<PmsGroupBuyingInfoVo> infoByPid(@Param("pid") String pid);
}
