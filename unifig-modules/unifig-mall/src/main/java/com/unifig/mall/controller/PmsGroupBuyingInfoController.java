package com.unifig.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.async.CloseGroupBuyingOrder;
import com.unifig.mall.bean.model.PmsGroupBuying;
import com.unifig.mall.bean.model.PmsGroupBuyingInfo;
import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoList;
import com.unifig.mall.bean.vo.PmsGroupBuyingInfoVo;
import com.unifig.mall.service.PmsGroupBuyingInfoService;
import com.unifig.mall.service.PmsGroupBuyingService;
import com.unifig.mall.bean.vo.JoinUserVo;
import com.unifig.mall.service.PmsGroupBuyingUserService;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Commodity group purchase
 * </p>
 *
 *
 * @since 2019-01-23
 */
@RestController
@RequestMapping("/pmsGroupBuyingInfo")
@Api(tags = "团购管理",description = "PmsGroupBuyingInfoController")
//@ApiIgnore
@Slf4j
public class PmsGroupBuyingInfoController {

    @Autowired
    private PmsGroupBuyingInfoService pmsGroupBuyingInfoService;

    @Autowired
    private PmsGroupBuyingService pmsGroupBuyingService;

    @Autowired
    private PmsGroupBuyingUserService pmsGroupBuyingUserService;

    @Autowired
    private CloseGroupBuyingOrder closeGroupBuyingOrder;

    @ApiOperation(value = "发起团购")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultData create(@ApiParam("团购信息") @RequestBody PmsGroupBuyingInfo pmsGroupBuyingInfo) {
        log.info("正在发起团购,商品id{},订单id{}",pmsGroupBuyingInfo.getProductId(),pmsGroupBuyingInfo.getOrderId());
        EntityWrapper<PmsGroupBuying> wp = new EntityWrapper<PmsGroupBuying>();
        wp.eq("status","1");
        wp.eq("enable","0");
        wp.eq("product_id",pmsGroupBuyingInfo.getProductId());
        List<PmsGroupBuying> pmsGroupBuyings = pmsGroupBuyingService.selectList(wp);
        if(pmsGroupBuyings != null){

            PmsGroupBuying pmsGroupBuying = pmsGroupBuyingService.selectById(pmsGroupBuyings.get(0).getId());
            if(!pmsGroupBuyingInfoService.isQualified(pmsGroupBuying)){
                //参团失败关闭订单并退回金额
                closeGroupBuyingOrder.closeOrder(pmsGroupBuyingInfo.getOrderId());
                return ResultData.result(false).setMsg("超过参团次数限制");
            }
            PmsGroupBuyingInfo insert = pmsGroupBuyingInfoService.createInfo(pmsGroupBuyingInfo,pmsGroupBuying);
            if (insert != null) {
                //添加参团记录
                pmsGroupBuyingUserService.record(insert.getId(), PmsGroupBuyingUserService.USER_TYPE_INITIATE,pmsGroupBuyingInfo.getOrderId());
                return ResultData.result(true).setData(insert);
            } else {
                return ResultData.result(false);
            }
        }
        return ResultData.result(false);
    }

    @ApiOperation(value = "参加团购")
    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public ResultData join(@ApiParam("团购id") @RequestParam String id,@ApiParam(name = "团购id",required = false)@RequestParam(value = "orderId",required = false) String orderId,@CurrentUser UserCache userCache) {
        ResultData result = pmsGroupBuyingInfoService.join(id,userCache.getUserId(),orderId);
        //添加参团记录
        if(result.getCode()==ResultData.SUCCESS){
            pmsGroupBuyingUserService.record(id, PmsGroupBuyingUserService.USER_TYPE_JOIN,orderId);
        }
        return result;
    }

    /**
     * 用户端商品参团列表
     * @return
     */
    @ApiOperation(value = "商品参团列表",notes = "查看该商品正在参团列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultData<PmsGroupBuyingInfo> list(@RequestParam(required = false, defaultValue = "0") Integer page,
                           @RequestParam(required = false, defaultValue = "10") Integer rows,
                           @ApiParam("商品id")@RequestParam String productId,
                           @ApiParam("状态 0拼团中  1拼团成功 2 拼团失败") @RequestParam(required = false, defaultValue = "0") Integer status) {
        ResultData<PmsGroupBuyingInfo> list = pmsGroupBuyingInfoService.selectList(page,rows,productId,status);
        return ResultData.result(true).setData(list).setMsg("success");
    }


    /**
     * 用户端根据团购id获取参团用户列表
     * @return
     */
    @ApiOperation(value = "用户端根据团购id获取参团用户列表")
    @RequestMapping(value = "/selectByGroupBuyingId", method = RequestMethod.GET)
    public ResultData<PmsGroupBuyingInfo> selectByGroupBuyingIdlist(
                                               @ApiParam("团购id")@RequestParam(value = "groupBuyingId") String groupBuyingId) {
        ResultData<PmsGroupBuyingInfo> list = pmsGroupBuyingInfoService.selectByGroupBuyingIdlist(groupBuyingId);
        return ResultData.result(true).setData(list).setMsg("success");
    }

    /**
     * 用户端根据商品id获取当前用户团购id
     * @return
     */
    @ApiOperation(value = "用户端根据商品id获取当前用户团购id")
    @RequestMapping(value = "/selectByProductId", method = RequestMethod.GET)
    public ResultData selectByProductId(
            @ApiParam("商品id")@RequestParam(value = "productId") String productId,@CurrentUser UserCache userCache) {
        return ResultData.result(true).setData(pmsGroupBuyingInfoService.selectByProductId(productId,userCache.getUserId())).setMsg("success");
    }

    /**
     * 团购配置团购列表
     * @return
     */
    @ApiOperation(value = "参团列表(管理端)",notes = "参团列表(管理端)")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResultData<PmsGroupBuyingInfoList> all(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(required = false, defaultValue = "10") Integer rows,
                                                   @ApiParam("团购配置(规则)id")@RequestParam(required = false, name = "rulesId") String rulesId,
                                                   @ApiParam("状态 0拼团中  1拼团成功 2 拼团失败") @RequestParam(required = false, name = "status") Integer status) {
        return  pmsGroupBuyingInfoService.info(page,rows,rulesId,status);
    }

    /**
     * 参团详情
     * @return
     */
    @ApiOperation(value = "参团详情(管理端)",notes = "参团详情(管理端)")
    @ApiModelProperty(name ="团购id",value = "pid")
    @RequestMapping(value = "/info/{pid}", method = RequestMethod.GET)
    public ResultData<PmsGroupBuyingInfoVo> info(@PathVariable("pid") String pid) {
        return  pmsGroupBuyingInfoService.infoByPid(pid);
    }

    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每5分钟扫描一次，扫描设定超时时间之前下的订单，如果没成功,关闭团购
     */
    @Scheduled(cron = "0 0/1 * ? * ?")
    public void close() {
        pmsGroupBuyingInfoService.close();
    }

}

