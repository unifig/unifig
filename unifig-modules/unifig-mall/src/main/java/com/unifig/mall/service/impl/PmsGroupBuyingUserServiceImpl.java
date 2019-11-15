package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.unifig.mall.bean.vo.PmsGroupBuyingUserVo;
import com.unifig.mall.feign.UmsMemberFeign;
import com.unifig.mall.mapper.PmsGroupBuyingUserMapper;
import com.unifig.mall.service.PmsGroupBuyingUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.model.UmsMember;
import com.unifig.result.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户参团记录 服务实现类
 * </p>
 *
 *
 * @since 2019-06-25
 */
@Service
@Slf4j
public class PmsGroupBuyingUserServiceImpl extends ServiceImpl<PmsGroupBuyingUserMapper, PmsGroupBuyingUser> implements PmsGroupBuyingUserService {

    @Autowired
    private PmsGroupBuyingUserMapper pmsGroupBuyingUserMapper;

    @Autowired
    private UmsMemberFeign umsMemberFeign;

    @Override
    public boolean record(String parentId, Integer type,String orderId) {
        try{
            UmsMember currentMember = umsMemberFeign.getCurrentMember();
            PmsGroupBuyingUser user = new PmsGroupBuyingUser();
            user.setParentId(parentId);
            user.setCreateTime(new Date());
            user.setType(type);
            user.setUserId(currentMember.getId().toString());
            user.setUserName(currentMember.getNickname());
            user.setUserPic(currentMember.getAvatar());
            if(orderId!=null){
                user.setOrderId(orderId);
            }
            pmsGroupBuyingUserMapper.insertAllColumn(user);
            return true;
        }catch (Exception e){
            log.error(e.getLocalizedMessage());
        }
        return false;
    }

    @Override
    public ResultData list(Integer pageSize, Integer pageNum, Integer type, String userId) {
        Page<PmsGroupBuyingUserVo> pmsGroupBuyingInfoPage = new Page<>(pageSize, pageNum);
        List<PmsGroupBuyingUserVo> list = baseMapper.selectListByUserId(pmsGroupBuyingInfoPage,type,userId);
        list.forEach(li->{
            EntityWrapper<PmsGroupBuyingUser> PMBUwrapper = new EntityWrapper<PmsGroupBuyingUser>();
            PMBUwrapper.eq("parent_id",li.getParentId());
            PMBUwrapper.orderBy("create_time");
            List<PmsGroupBuyingUser> PmsGroupBuyingUser = pmsGroupBuyingUserMapper.selectList(PMBUwrapper);
            li.setUserVoList(PmsGroupBuyingUser);
        });
        return ResultData.result(true).setData(list).setCount(baseMapper.selectListByUserId(new Page<>(0, 1000000),type,userId).size());
    }
}
