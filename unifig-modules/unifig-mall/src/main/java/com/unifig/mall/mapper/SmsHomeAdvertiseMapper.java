package com.unifig.mall.mapper;

import com.unifig.mall.bean.model.SmsHomeAdvertise;
import com.unifig.mall.bean.model.SmsHomeAdvertiseExample;
import com.unifig.mall.bean.vo.HomeSmsAdvertiseVo;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

public interface SmsHomeAdvertiseMapper {
    int countByExample(SmsHomeAdvertiseExample example);

    int deleteByExample(SmsHomeAdvertiseExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsHomeAdvertise record);

    int insertSelective(SmsHomeAdvertise record);

    List<SmsHomeAdvertise> selectByExample(SmsHomeAdvertiseExample example);

    SmsHomeAdvertise selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsHomeAdvertise record, @Param("example") SmsHomeAdvertiseExample example);

    int updateByExample(@Param("record") SmsHomeAdvertise record, @Param("example") SmsHomeAdvertiseExample example);

    int updateByPrimaryKeySelective(SmsHomeAdvertise record);

    int updateByPrimaryKey(SmsHomeAdvertise record);

    List<HomeSmsAdvertiseVo> selectHomeList();

    List<HomeSmsAdvertiseVo>  selectHomeListByType(@Param("type") Integer type, @Param("onlineTime")Date onlineTime);

}