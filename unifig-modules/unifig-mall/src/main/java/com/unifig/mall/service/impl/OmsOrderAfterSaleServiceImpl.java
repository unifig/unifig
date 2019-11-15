/**
 * FileName: OmsOrderAfterSaleServiceImpl
 * Author:
 * Date:     2019-10-31 14:29
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.async.GroupBuyingOrder;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderAfterSale;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.unifig.mall.feign.UmsMemberFeign;
import com.unifig.mall.mapper.OmsOrderAfterSaleMapper;
import com.unifig.mall.mapper.OmsOrderMapper;
import com.unifig.mall.mapper.PmsGroupBuyingMapper;
import com.unifig.mall.service.OmsOrderAfterSaleService;
import com.unifig.mall.service.OmsPayService;
import com.unifig.model.UmsMember;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <h3>概要:</h3><p>OmsOrderAfterSaleServiceImpl</p>
 * <h3>功能:</h3><p></p>
 *
 * @create 2019-10-31
 * @since 1.0.0
 */
@Service
public class OmsOrderAfterSaleServiceImpl extends ServiceImpl<OmsOrderAfterSaleMapper, OmsOrderAfterSale> implements OmsOrderAfterSaleService {

    @Autowired
    private OmsOrderAfterSaleMapper omsOrderAfterSaleMapper;

    @Autowired
    private UmsMemberFeign umsMemberFeign;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsPayService omsPayService;

    @Override
    public ResultData create(OmsOrderAfterSale returnApply) {
        UmsMember user = umsMemberFeign.getCurrentMember();
        returnApply.setUserId(user.getId().toString());
        returnApply.setUserName(user.getNickname());
        returnApply.setUserPic(user.getAvatar());
        returnApply.setCreateTime(new Date());
        returnApply.setStatus(0);
        omsOrderAfterSaleMapper.insert(returnApply);
        //更改订单状态
        updateOrder(returnApply.getOrderId(),7);
        //TODO 更改订单状态
        return ResultData.result(true).setData(returnApply).setMsg("success");
    }

    @Override
    public ResultData list(Integer page, Integer rows, String createTime, String updateTime, Integer status) {
        EntityWrapper<OmsOrderAfterSale> wrapper = new EntityWrapper<OmsOrderAfterSale>();
        if(StringUtils.hasText(createTime)){
            wrapper.eq("create_time",createTime);
        }
        if(StringUtils.hasText(updateTime)){
            wrapper.eq("update_time",updateTime);
        }
        if(status != null){
            wrapper.eq("status",status);
        }
        List<OmsOrderAfterSale> records = omsOrderAfterSaleMapper.selectPage(new Page<PmsGroupBuyingInfo>(page, rows), wrapper);
        return ResultData.result(true).setData(records).setCount(omsOrderAfterSaleMapper.selectCount(wrapper)).setMsg("success");
    }

    @Override
    public ResultData answer(String id) {
        OmsOrderAfterSale omsOrderAfterSale = omsOrderAfterSaleMapper.selectById(id);
        omsOrderAfterSale.setUpdateTime(new Date());
        omsOrderAfterSale.setStatus(1);
        omsOrderAfterSaleMapper.updateById(omsOrderAfterSale);
        //TODO 更改订单状态
        return ResultData.result(true).setData(omsOrderAfterSale).setMsg("success");
    }

    @Override
    public ResultData refund(String id, String money, String type) {
        if(!StringUtils.hasText(type)){
            return ResultData.result(false).setData(null).setMsg("请选择操作类型");
        }
        OmsOrderAfterSale omsOrderAfterSale = omsOrderAfterSaleMapper.selectById(id);
        if(type.equals("1")){
            omsOrderAfterSale.setUpdateTime(new Date());
            omsOrderAfterSale.setStatus(3);
            //更改订单状态
            updateOrder(id,10);
            omsOrderAfterSaleMapper.updateById(omsOrderAfterSale);
        }else if(type.equals("0")){
            omsOrderAfterSale.setUpdateTime(new Date());
            omsOrderAfterSale.setStatus(2);
            //退款
            omsPayService.refund(Long.valueOf(id),new Double(money));
            //更改订单状态
            updateOrder(id,9);
            omsOrderAfterSaleMapper.updateById(omsOrderAfterSale);
        }
        return ResultData.result(false).setData(null).setMsg("操作失败");
    }

    /**
     * 更改订单状态
     * @param id
     * 7 -> 申请退款;8-> 退款中;9 退款完成 ;10-> 拒绝退款
     */
    public void updateOrder(String id,Integer status){
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(Long.valueOf(id));
        if(omsOrder !=null ){
            omsOrder.setStatus(status);
            orderMapper.updateByPrimaryKey(omsOrder);
        }
    }
}
