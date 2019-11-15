package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.unifig.mall.async.CloseGroupBuyingOrder;
import com.unifig.mall.async.GroupBuyingOrder;
import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.unifig.mall.bean.vo.JoinUserVo;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoList;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoVo;
import com.unifig.mall.feign.UmsMemberFeign;
import com.unifig.mall.mapper.PmsGroupBuyingMapper;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.unifig.mall.mapper.PmsGroupBuyingInfoMapper;
import com.unifig.mall.mapper.PmsGroupBuyingUserMapper;
import com.unifig.mall.service.OmsPayService;
import com.unifig.mall.service.PmsGroupBuyingInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.service.PmsGroupBuyingUserService;
import com.unifig.model.UmsMember;
import com.unifig.result.ResultData;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品团购子表 服务实现类
 * </p>
 *
 *
 * @since 2019-01-23
 */
@Service
@Slf4j
public class PmsGroupBuyingInfoServiceImpl extends ServiceImpl<PmsGroupBuyingInfoMapper, PmsGroupBuyingInfo> implements PmsGroupBuyingInfoService {

    @Autowired
    private PmsGroupBuyingInfoMapper pmsGroupBuyingInfoMapper;

    @Autowired
    private PmsGroupBuyingMapper pmsGroupBuyingMapper;

    @Autowired
    private PmsGroupBuyingUserMapper pmsGroupBuyingUserMapper;

    @Autowired
    private CloseGroupBuyingOrder closeGroupBuyingOrder;

    @Autowired
    private OmsPayService omsPayService;

    @Autowired
    private UmsMemberFeign umsMemberFeign;

    @Autowired
    private GroupBuyingOrder groupBuyingOrder;

    @Autowired
    private PmsGroupBuyingUserService pmsGroupBuyingUserService;

    @Override
    public PmsGroupBuyingInfo createInfo(PmsGroupBuyingInfo pmsGroupBuyingInfo,PmsGroupBuying pmsGroupBuying) {
        pmsGroupBuyingInfo.setStartTime(new Date());
        if(pmsGroupBuying.getValidTime() != null){
            pmsGroupBuyingInfo.setEndTime(new Date(System.currentTimeMillis()+pmsGroupBuying.getValidTime()));
        }else{
            pmsGroupBuyingInfo.setEndTime(pmsGroupBuying.getEndTime());
        }
        pmsGroupBuyingInfo.setGroupBuyingId(pmsGroupBuying.getId());
        pmsGroupBuyingInfo.setSuccessNumber(pmsGroupBuying.getNumber());
        pmsGroupBuyingInfoMapper.insert(pmsGroupBuyingInfo);
        PmsGroupBuying pmsGroupBuying1 = pmsGroupBuyingMapper.selectById(pmsGroupBuyingInfo.getGroupBuyingId());
        //一人参团订单是直接成功
        if(pmsGroupBuying1.getNumber().equals(pmsGroupBuying1.getLimitation())){
            pmsGroupBuyingInfo.setStatus(CONSTANT_GROUP_STATUS_SUCCESS);
            pmsGroupBuyingMapper.updateById(pmsGroupBuying);

            log.info("更改订单状态为待发货");
            groupBuyingOrder.updateOrder(pmsGroupBuyingInfo.getId());
        }
        return pmsGroupBuyingInfo;
    }

    @Override
    public ResultData join(String id,String userId,String orderId) {
        PmsGroupBuyingInfo pmsGroupBuyingInfo = pmsGroupBuyingInfoMapper.selectById(id);
        PmsGroupBuying groupBuying = pmsGroupBuyingMapper.selectById(pmsGroupBuyingInfo.getGroupBuyingId());
        //TODO 暂时限制参团人数
        EntityWrapper<PmsGroupBuyingUser> wrapper = new EntityWrapper<PmsGroupBuyingUser>();
        wrapper.eq("parent_id",id);
        wrapper.eq("user_id",userId);
        List<PmsGroupBuyingUser> pmsGroupBuyingUsers = pmsGroupBuyingUserMapper.selectList(wrapper);
        if(pmsGroupBuyingUsers.size()>0){
            //参团失败关闭订单并退回金额
            closeGroupBuyingOrder.closeOrder(orderId);
            return ResultData.result(false).setMsg("参团失败,参团超过最大限制数").setCode(CONSTANT_GROUP_REPETITION);
        }
        if(pmsGroupBuyingInfo == null){
            //参团失败关闭订单并退回金额
            closeGroupBuyingOrder.closeOrder(orderId);
            return ResultData.result(false).setMsg("参团失败,团购不存在").setCode(CONSTANT_GROUP_NONENTITY);
        }
        if(pmsGroupBuyingInfo.getEndTime().getTime() <= System.currentTimeMillis()){
            //参团失败关闭订单并退回金额
            closeGroupBuyingOrder.closeOrder(orderId);
            return ResultData.result(false).setMsg("参团失败,团购超时").setCode(CONSTANT_GROUP_TIMEOUT);
        }
        if(pmsGroupBuyingInfo.getNumber() >= pmsGroupBuyingInfo.getSuccessNumber()){
            //参团失败关闭订单并退回金额
            closeGroupBuyingOrder.closeOrder(orderId);
            return ResultData.result(false).setMsg("参团失败,团购已满").setCode(CONSTANT_GROUP_FULL_HOUSE);
        }if(!pmsGroupBuyingInfo.getStatus().equals(CONSTANT_GROUP_STATUS_IN_SPELLING) && groupBuying.getStatus() == 2){
            //参团失败关闭订单并退回金额
            closeGroupBuyingOrder.closeOrder(orderId);
            return ResultData.result(false).setMsg("参团失败,团购已关闭").setCode(CONSTANT_GROUP_CLOSE);
        }
        pmsGroupBuyingInfo.setNumber(pmsGroupBuyingInfo.getNumber()+1);
        Integer integer = pmsGroupBuyingInfoMapper.updateById(pmsGroupBuyingInfo);
        log.info("添加用户参团记录");
        pmsGroupBuyingUserService.record(id, PmsGroupBuyingUserService.USER_TYPE_JOIN,orderId);
        if(pmsGroupBuyingInfo.getNumber().equals(pmsGroupBuyingInfo.getSuccessNumber())){
            pmsGroupBuyingInfo.setStatus(CONSTANT_GROUP_STATUS_SUCCESS);
            pmsGroupBuyingMapper.updateById(groupBuying);

            log.info("更改订单状态为待发货");
            groupBuyingOrder.updateOrder(pmsGroupBuyingInfo.getId());
        }
        return ResultData.result(true).setData(pmsGroupBuyingInfo).setMsg("参团成功");
    }

    @Override
    public void close() {
        long currentTime = System.currentTimeMillis();
        EntityWrapper<PmsGroupBuyingInfo> wrapper = new EntityWrapper<PmsGroupBuyingInfo>();
        wrapper.eq("status", CONSTANT_GROUP_STATUS_IN_SPELLING);
        List<PmsGroupBuyingInfo> pmsGroupBuyingInfos = pmsGroupBuyingInfoMapper.selectList(wrapper);
        pmsGroupBuyingInfos.forEach(li -> {
            if(currentTime >= li.getEndTime().getTime()){
                li.setStatus(CONSTANT_GROUP_STATUS_BE_DEFEATED);
                pmsGroupBuyingInfoMapper.updateById(li);
                log.info("关闭团购id:{}",li.getId());

                //异步调取失败退款流程
                closeGroupBuyingOrder.close(li.getId());

            }
        });
    }

    @Override
    public ResultData<PmsGroupBuyingInfo> selectList(Integer page, Integer rows, String productId, Integer status) {
        //商品id为空不在继续操作
        if(!StringUtils.hasText(productId)){
            return ResultData.result(true).setMsg("Item id cannot be empty");
        }

        EntityWrapper<PmsGroupBuyingInfo> wrapper = new EntityWrapper<PmsGroupBuyingInfo>();
        wrapper.eq("status", status);
        wrapper.le("start_time",new Date());
        wrapper.ge("end_time",new Date());

        //团购配置ids
        List<String> groupBuyingIds = new ArrayList<>();

        //查询团购配置表获取团购配置信息
        EntityWrapper<PmsGroupBuying> wp = new EntityWrapper<PmsGroupBuying>();
        wp.eq("status","1");//0保存  1开启 2 关闭
        wp.eq("enable","0");// 0 否  1 删除
        wp.eq("product_id",productId);
        List<PmsGroupBuying> pmsGroupBuyings = pmsGroupBuyingMapper.selectList(wp);
        if(pmsGroupBuyings.size() <= 0){
            return ResultData.result(true).setMsg("This item is not available for group purchase");
        }

        pmsGroupBuyings.forEach(li -> groupBuyingIds.add(li.getId()));
        wrapper.in("group_buying_id",groupBuyingIds);
        List<PmsGroupBuyingInfo> records = pmsGroupBuyingInfoMapper.selectPage(new Page<PmsGroupBuyingInfo>(page, rows), wrapper);
        //查询参团列表
        records.forEach(li ->{
            EntityWrapper<PmsGroupBuyingUser> PMBUwrapper = new EntityWrapper<PmsGroupBuyingUser>();
            PMBUwrapper.eq("parent_id",li.getId());
            PMBUwrapper.orderBy("create_time");
            List<PmsGroupBuyingUser> PmsGroupBuyingUser = pmsGroupBuyingUserMapper.selectList(PMBUwrapper);
            li.setUserVoList(PmsGroupBuyingUser);
        });
        Integer count = pmsGroupBuyingInfoMapper.selectCount(wrapper);
        if(records.size()>0){
            return ResultData.result(true).setCount(count).setData(records);
        }else{
            return ResultData.result(false).setMsg("There is no group information for this product");
        }
    }

    @Override
    public ResultData info(Integer page, Integer rows, String groupBuyingId, Integer status) {
        Page<PmsGroupBuyingInfoList> pmsGroupBuyingInfoPage = new Page<>(page, rows);
        pmsGroupBuyingInfoPage.setRecords(baseMapper.selectByList(pmsGroupBuyingInfoPage,groupBuyingId,status));
        return ResultData.result(true).setCount(baseMapper.selectByList(new Page<>(0, 100000),groupBuyingId,status).size()).setData(pmsGroupBuyingInfoPage.getRecords());
    }

    @Override
    public ResultData<PmsGroupBuyingInfoVo> infoByPid(String pid) {
       List<PmsGroupBuyingInfoVo> list =  pmsGroupBuyingInfoMapper.infoByPid(pid);
        return ResultData.result(true).setCount(list.size()).setData(list);
    }

    @Override
    public ResultData<PmsGroupBuyingInfo> selectByGroupBuyingIdlist(String groupBuyingId) {
        if(!StringUtils.hasText(groupBuyingId)){
            return ResultData.result(true).setMsg("groupBuying id cannot be empty");
        }

        List<PmsGroupBuyingInfo> records = new ArrayList<> ();
        Integer count = 0;
        PmsGroupBuyingInfo pmsGroupBuyingInfo = pmsGroupBuyingInfoMapper.selectById(groupBuyingId);
        if (pmsGroupBuyingInfo.getStatus() == 0 && pmsGroupBuyingInfo.getNumber()<pmsGroupBuyingInfo.getSuccessNumber()) {
            EntityWrapper<PmsGroupBuyingUser> PMBUwrapper = new EntityWrapper<PmsGroupBuyingUser>();
            PMBUwrapper.eq("parent_id",groupBuyingId);
            PMBUwrapper.orderBy("create_time");
            List<PmsGroupBuyingUser> PmsGroupBuyingUser = pmsGroupBuyingUserMapper.selectList(PMBUwrapper);
            pmsGroupBuyingInfo.setUserVoList(PmsGroupBuyingUser);
            records.add(pmsGroupBuyingInfo);
            count = records.size();
        }else{
            String productId = pmsGroupBuyingInfo.getProductId();
            EntityWrapper<PmsGroupBuyingInfo> wrapper = new EntityWrapper<PmsGroupBuyingInfo>();
            wrapper.eq("status", 0);
            wrapper.le("start_time",new Date());
            wrapper.ge("end_time",new Date());

            //团购配置ids
            List<String> groupBuyingIds = new ArrayList<>();

            //查询团购配置表获取团购配置信息
            EntityWrapper<PmsGroupBuying> wp = new EntityWrapper<PmsGroupBuying>();
            wp.eq("status","1");//0保存  1开启 2 关闭
            wp.eq("enable","0");// 0 否  1 删除
            wp.eq("product_id",productId);
            List<PmsGroupBuying> pmsGroupBuyings = pmsGroupBuyingMapper.selectList(wp);
            if(pmsGroupBuyings.size() <= 0){
                return ResultData.result(true).setMsg("This item is not available for group purchase");
            }
            groupBuyingIds.add(groupBuyingId);
            pmsGroupBuyings.forEach(li -> groupBuyingIds.add(li.getId()));
            wrapper.in("group_buying_id",groupBuyingIds);
            records = pmsGroupBuyingInfoMapper.selectList(wrapper);
            //查询参团列表
            records.forEach(li ->{
                EntityWrapper<PmsGroupBuyingUser> PMBUwrapper = new EntityWrapper<PmsGroupBuyingUser>();
                PMBUwrapper.eq("parent_id",li.getId());
                PMBUwrapper.orderBy("create_time");
                List<PmsGroupBuyingUser> PmsGroupBuyingUser = pmsGroupBuyingUserMapper.selectList(PMBUwrapper);
                li.setUserVoList(PmsGroupBuyingUser);
            });
            count = pmsGroupBuyingInfoMapper.selectCount(wrapper);

        }
        if(records.size()>0){
            return ResultData.result(true).setCount(count).setData(records);
        }else{
            return ResultData.result(false).setMsg("There is no group information for this product");
        }
    }

    @Override
    public Object selectByProductId(String productId, String userId) {
        EntityWrapper<PmsGroupBuying> wrapper = new EntityWrapper<PmsGroupBuying>();
        wrapper.eq("product_id",productId);
        wrapper.eq("enable",0);
        List<PmsGroupBuying> pmsGroupBuyings = pmsGroupBuyingMapper.selectList(wrapper);
        //初始化团购配置id集合
        List<String> pid = new ArrayList<>();
        pmsGroupBuyings.forEach(li->{
            pid.add(li.getId());
        });
        if(pid != null && pid.size()>0){
            EntityWrapper<PmsGroupBuyingInfo> wrapperInfo = new EntityWrapper<PmsGroupBuyingInfo>();
            wrapperInfo.in("group_buying_id",pid);
            wrapperInfo.eq("status",0);
            List<PmsGroupBuyingInfo> pmsGroupBuyingInfos = pmsGroupBuyingInfoMapper.selectList(wrapperInfo);
            List<String> groupBuyings = new ArrayList<>();
            pmsGroupBuyingInfos.forEach(li->{
                groupBuyings.add(li.getId());
            });
            if(groupBuyings != null && groupBuyings.size()>0){
                EntityWrapper<PmsGroupBuyingUser> wrapperUser = new EntityWrapper<PmsGroupBuyingUser>();
                wrapperUser.in("parent_id",groupBuyings);
                List<PmsGroupBuyingUser> pmsGroupBuyingUsers = pmsGroupBuyingUserMapper.selectList(wrapperUser);
                if(pmsGroupBuyingUsers!= null && pmsGroupBuyingUsers.size()>0){
                    return pmsGroupBuyingUsers.get(0).getParentId();
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public boolean isQualified(PmsGroupBuying pmsGroupBuying) {
        log.info("开始判断是否超过团购次数,团购配置id{}",pmsGroupBuying.getId());
        log.info("团购配置允许最大参团(发团)次数{}",pmsGroupBuying.getLimitation());
        UmsMember currentMember = umsMemberFeign.getCurrentMember();
        log.info("用户信息:昵称={},用户id={}",currentMember.getNickname(),currentMember.getId());
        EntityWrapper<PmsGroupBuyingInfo> wrapperInfo = new EntityWrapper<PmsGroupBuyingInfo>();
        wrapperInfo.in("group_buying_id",pmsGroupBuying.getId());
        wrapperInfo.ne("status",2);
        List<PmsGroupBuyingInfo> pmsGroupBuyingInfos = pmsGroupBuyingInfoMapper.selectList(wrapperInfo);
        log.info("全部可拼团数量{}",pmsGroupBuyingInfos.size());
        List<String> groupBuyings = new ArrayList<>();
        pmsGroupBuyingInfos.forEach(li->{
            groupBuyings.add(li.getId());
        });
        if(groupBuyings != null && groupBuyings.size()>0){
            EntityWrapper<PmsGroupBuyingUser> wrapperUser = new EntityWrapper<PmsGroupBuyingUser>();
            wrapperUser.eq("user_id",currentMember.getId());
            wrapperUser.in("parent_id",groupBuyings);
            List<PmsGroupBuyingUser> pmsGroupBuyingUsers = pmsGroupBuyingUserMapper.selectList(wrapperUser);
            log.info("用户以参团数量{}",pmsGroupBuyingUsers.size());
            if(pmsGroupBuyingUsers!= null){
                //判断是否超出次数限制
                if(pmsGroupBuyingUsers.size()<pmsGroupBuying.getLimitation()){
                    log.info("允许参团");
                    return true;
                }else{
                    return false;
                }
            }
            return false;
        }
        return true;
    }
}
