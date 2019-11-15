/**
 * FileName: 订单配送
 * Author:
 * Date:     2019-07-24
 * Description: 订单配送
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.service.impl;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.mall.bean.dto.OmsOrderDetail;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.bean.model.OmsOrderDeliveryman;
import com.unifig.mall.mapper.OmsOrderDeliverymanMapper;
import com.unifig.mall.mapper.OmsOrderMapper;
import com.unifig.mall.service.OmsOrderDeliverymanService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.mall.service.OmsOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.unifig.result.ResultData;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单配送 服务实现类
 * </p>
 *
 *
 * @since 2019-07-24
 */
@Service
public class OmsOrderDeliverymanServiceImpl extends ServiceImpl<OmsOrderDeliverymanMapper, OmsOrderDeliveryman> implements OmsOrderDeliverymanService {

	private Logger logger=LoggerFactory.getLogger(getClass());

    @Autowired
    private OmsOrderDeliverymanMapper omsOrderDeliverymanMapper;

    @Autowired
    private OmsOrderService orderService;

    @Autowired
    private OmsOrderMapper orderMapper;

    /**
    * 查询分页数据
     */
    @Override
    public ResultData<OmsOrderDeliveryman> findListByPage(int pageNum,int pageSize){
		Integer count = omsOrderDeliverymanMapper.selectCount(null);
		List<OmsOrderDeliveryman> omsOrderDeliverymanList = omsOrderDeliverymanMapper.selectPage(new Page<OmsOrderDeliveryman>(pageNum, pageSize), null);
		return ResultData.result(true).setData(omsOrderDeliverymanList).setCount(count);
    }


    /**
    * 根据id查询
    */
    @Override
    public ResultData<OmsOrderDeliveryman> getById(String id){
        OmsOrderDeliveryman omsOrderDeliveryman = omsOrderDeliverymanMapper.selectById(id);
		return ResultData.result(true).setData(omsOrderDeliveryman);
    }

    /**
    * 新增
    */
    @Override
    public ResultData add(List<String> ids,String userId,String userName){
        if(null!=ids&&ids.size()>0){
            for (String id : ids) {
                OmsOrderDetail detail = orderService.detail(Long.valueOf(id));
                if(detail == null){
                    continue;
                }
                OmsOrderDeliveryman omsOrderDeliveryman = new OmsOrderDeliveryman();
                omsOrderDeliveryman.setName(detail.getReceiverName());
                omsOrderDeliveryman.setCity(detail.getReceiverCity());
                omsOrderDeliveryman.setPhoneNumber(detail.getReceiverPhone());
                omsOrderDeliveryman.setProvince(detail.getReceiverProvince());
                omsOrderDeliveryman.setDetailAddress(detail.getReceiverDetailAddress());
                omsOrderDeliveryman.setOrderId(id);
                omsOrderDeliveryman.setCreateTime(new Date());
                omsOrderDeliveryman.setStatus(DELIVERYMAN_STATUS_DEFAULT);
                omsOrderDeliveryman.setDeliverymanId(userId);
                omsOrderDeliveryman.setDeliverymanName(userName);
                omsOrderDeliverymanMapper.insert(omsOrderDeliveryman);

                //修改订单状态为发货状态
                OmsOrder order = detail;
                order.setStatus(2);
                orderMapper.updateByPrimaryKey(order);
            }
            return ResultData.result(true).setMsg("分配成功");
        }else{
            return ResultData.result(true).setMsg("分配失败,请检查ids是否为空!");
        }
    }

    /**
    * 删除
    */
    @Override
    public ResultData delete(List<String> ids){
    	if(null!=ids&&ids.size()>0){
		    for (String id : ids) {
                omsOrderDeliverymanMapper.deleteById(id);
		    }
		    return ResultData.result(true).setMsg("删除成功");
        }else{
		    return ResultData.result(true).setMsg("删除失败,请检查ids是否为空!");
        }
    }

    /**
    * 修改
     */
    @Override
    public ResultData update(String uuid){
        OmsOrderDeliveryman omsOrderDeliveryman = new OmsOrderDeliveryman();
        omsOrderDeliveryman.setUuid(uuid);
        omsOrderDeliveryman.setStatus(DELIVERYMAN_STATUS_ACCOMPLISH);
        omsOrderDeliveryman.setAccomplishTime(new Date());
        omsOrderDeliverymanMapper.updateById(omsOrderDeliveryman);

        //更改订单状态
        OmsOrderDetail detail = orderService.detail(Long.valueOf(omsOrderDeliveryman.selectById().getOrderId()));
        OmsOrder order = detail;
        order.setStatus(3);
        orderMapper.updateByPrimaryKey(order);
		return ResultData.result(true).setMsg("配送完成");
    }

    @Override
    public ResultData<OmsOrderDeliveryman> clientList(int page, int rows, int status, String userId) {
        EntityWrapper<OmsOrderDeliveryman> wrapper = new EntityWrapper<OmsOrderDeliveryman>();
        wrapper.eq("status", status);
        wrapper.eq("deliveryman_id",userId);
        wrapper.orderBy("create_time");
        List<OmsOrderDeliveryman> omsOrderDeliverymen = omsOrderDeliverymanMapper.selectPage(new Page<OmsOrderDeliveryman>(page, rows), wrapper);
        if(omsOrderDeliverymen.size()>0){
            //查询订单商品信息
            omsOrderDeliverymen.forEach(li ->{
                OmsOrderDetail detail = orderService.detail(Long.valueOf(li.getOrderId()));
                li.setOrderItemList(detail != null? detail.getOrderItemList() != null? detail.getOrderItemList() : null : null);});
        }
        Integer count = omsOrderDeliverymanMapper.selectCount(wrapper);
        return ResultData.result(true).setData(omsOrderDeliverymen).setCount(count);
    }
}
