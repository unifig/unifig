package com.unifig.mall.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.vo.SmsCouponHistoryVo;
import com.unifig.mall.service.SmsCouponHistoryService;
import com.unifig.result.Rest;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺核销
 */
@RestController
@RequestMapping("/oms/client/shop/verify")
@Api(tags = "店铺核销", description = "OmsWriteOffCouponController")
@ApiIgnore
public class OmsShopVerifyCouponController {

    @Autowired
    private SmsCouponHistoryService historyService;

    @ApiOperation(value = "核销优惠券")
    @GetMapping(value = "/{code}")
    public ResultData verify(@PathVariable(value="code") String code,@CurrentUser UserCache userCache) {
        return historyService.verify(code,userCache.getUserId());
    }

    @ApiOperation(value = "核销优惠券查看")
    @GetMapping(value = "/select/{code}")
    public Rest<SmsCouponHistoryVo> selectVerify(@PathVariable(value="code") String code,@CurrentUser UserCache userCache) {
        SmsCouponHistoryVo vo =  historyService.selectVerify(code,userCache.getUserId());
        return Rest.resultData(new SmsCouponHistoryVo()).setData(vo);
    }

    @ApiOperation(value = "用户优惠券列表")
    @GetMapping(value = "/selectUserCoupon")
    @ApiImplicitParam(name = "status", value = "使用状态：0->未使用；1->已使用；2->已过期",defaultValue = "0", allowableValues = "0,1,2", paramType = "query", dataType = "integer")
    public RestList<SmsCouponHistoryVo> selectUserCouponList(@RequestParam(required = false, defaultValue = "0")Integer status,
                                                             @CurrentUser UserCache userCache,
                                                             @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<SmsCouponHistoryVo> list= historyService.selectUserCouponList(userCache.getUserId(),status);
        List<SmsCouponHistoryVo>  res = list.stream().skip(pageSize*(pageNum-1)).limit(pageSize).collect(Collectors.toList());
        return RestList.resultData(new SmsCouponHistoryVo()).setData(res).setCount(list.size());
    }

    @ApiOperation(value = "查看优惠券详情")
    @GetMapping(value = "/selectById")
    @ApiImplicitParam(name = "id", value = "优惠券id", paramType = "query", dataType = "integer")
    public Rest<SmsCouponHistoryVo> selectById(@RequestParam(required = true)Integer id,
                                                         @CurrentUser UserCache userCache) {
        SmsCouponHistoryVo data = historyService.selectById(userCache.getUserId(),id);
        return Rest.resultData(new SmsCouponHistoryVo()).setData(data);
    }
}
