package com.unifig.mall.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.dto.*;
import com.unifig.mall.bean.model.OmsOrder;
import com.unifig.mall.service.OmsOrderService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单管理Controller
 *    on 2018/10/11.
 */
@Controller
@Api(tags = "用户订单管理", description = "OmsOrderController")
@RequestMapping("/order")
//@ApiIgnore
public class OmsOrderController {
    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("查询订单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list(OmsOrderQueryParam queryParam,
                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsOrder> orderList = orderService.list(queryParam, pageSize, pageNum);
        return new CommonResult().pageSuccess(orderList);
    }

    @ApiOperation("查询订单client")
    @RequestMapping(value = "/clientList", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<OmsOrderDetail> clientList(OmsOrderQueryParam queryParam,
                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,@CurrentUser UserCache userCache) {
        queryParam.setMemberId(Long.valueOf(userCache.getUserId()));
        List<OmsOrder> orderList = orderService.list(queryParam, pageSize, pageNum);
        List<OmsOrderDetail> list = new ArrayList<OmsOrderDetail>();
        for (OmsOrder li :orderList){
            OmsOrderDetail orderDetailResult = orderService.detail(li.getId());
            list.add(orderDetailResult);
        }
        return ResultData.result(true).setData(list);
    }

    @ApiOperation("查询订单client状态")
    @RequestMapping(value = "/clientListStatistics", method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Integer> clientListStatistics(OmsOrderQueryParam queryParam,
                             @RequestParam(value = "pageSize", defaultValue = "10000") Integer pageSize,
                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,@CurrentUser UserCache userCache) {
        queryParam.setMemberId(Long.valueOf(userCache.getUserId()));
        List<OmsOrder> orderList = orderService.list(queryParam, pageSize, pageNum);
        Map<Integer,List<OmsOrder>> pmsUserShopsMap = orderList.stream().collect(Collectors.groupingBy(OmsOrder::getStatus));
        Map<String,Integer> map = new HashMap<>();
        map.put("payWait",pmsUserShopsMap.get(0) != null ? pmsUserShopsMap.get(0).size() : 0);
        map.put("payAfter",pmsUserShopsMap.get(1) != null ? pmsUserShopsMap.get(1).size() : 0);
        map.put("receivingWait",pmsUserShopsMap.get(2) != null ? pmsUserShopsMap.get(2).size() : 0);
        map.put("evaluateWait",pmsUserShopsMap.get(3) != null ? pmsUserShopsMap.get(3).size() : 0);
        map.put("retreatWait",pmsUserShopsMap.get(5) != null ? pmsUserShopsMap.get(5).size() : 0);
        return map;
    }

    @ApiOperation("批量发货")
    @RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("批量关闭订单")
    @RequestMapping(value = "/update/close", method = RequestMethod.POST)
    @ResponseBody
    public ResultData close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
        int count = orderService.close(ids, note);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("批量删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResultData delete(@RequestParam("ids") List<Long> ids) {
        int count = orderService.delete(ids);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(false);
    }

    @ApiOperation("获取订单详情:订单信息、商品信息、操作记录")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultData<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return ResultData.result(true).setData(orderDetailResult);
    }

    @ApiOperation("修改收货人信息")
    @RequestMapping(value = "/update/receiverInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultData updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return ResultData.result(true).setData(count);
        }
        return ResultData.result(true);
    }

    @ApiOperation("修改订单费用信息")
    @RequestMapping(value = "/update/moneyInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
        int count = orderService.updateMoneyInfo(moneyInfoParam);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("备注订单")
    @RequestMapping(value = "/update/note", method = RequestMethod.POST)
    @ResponseBody
    public Object updateNote(@RequestParam("id") Long id,
                             @RequestParam("note") String note,
                             @RequestParam("status") Integer status) {
        int count = orderService.updateNote(id, note, status);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

}
