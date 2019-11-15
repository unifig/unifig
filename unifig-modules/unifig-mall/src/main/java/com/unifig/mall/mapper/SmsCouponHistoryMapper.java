package com.unifig.mall.mapper;

import com.unifig.mall.bean.model.SmsCouponHistoryExample;
import com.unifig.mall.bean.vo.SmsCouponHistoryVo;
import com.unifig.model.SmsCouponHistory;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsCouponHistoryMapper {
    int countByExample(SmsCouponHistoryExample example);

    int deleteByExample(SmsCouponHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsCouponHistory record);

    int insertSelective(SmsCouponHistory record);

    List<SmsCouponHistory> selectByExample(SmsCouponHistoryExample example);

    SmsCouponHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsCouponHistory record, @Param("example") SmsCouponHistoryExample example);

    int updateByExample(@Param("record") SmsCouponHistory record, @Param("example") SmsCouponHistoryExample example);

    int updateByPrimaryKeySelective(SmsCouponHistory record);

    int updateByPrimaryKey(SmsCouponHistory record);

    List<SmsCouponHistoryVo> selectUserCouponList(@Param("userId") String userId,@Param("status") Integer status);

    List<String> selectShopVerifyCouponList(@Param("userId") String userId);

    SmsCouponHistoryVo selectVerify(@Param("code") String code, @Param("userId") String userId);

    void staleDated(@Param("couponId") Long couponId);
}