package com.unifig.mall.controller.client;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.mall.bean.dto.CommonResult;
import com.unifig.mall.service.SmsCouponHistoryService;
import com.unifig.mall.service.SmsCouponService;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCoupon;
import com.unifig.model.SmsCouponHistory;
import com.unifig.model.SmsCouponHistoryDetail;
import com.unifig.result.RestList;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 优惠券管理Controller
 *    on 2018/8/28.
 */
@RestController
@Api(tags = "优惠券管理", description = "SmsCouponController")
@RequestMapping("/client/coupon")
@ApiIgnore
public class SmsClinetCouponController {
    @Autowired
    private SmsCouponService couponService;

    @Autowired
    private SmsCouponHistoryService smsCouponHistoryService;

    @Autowired
    private SmsCouponHistoryService historyService;
    @ApiOperation("根据优惠券id，使用状态，订单编号分页获取领取记录")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public Object list(@RequestParam(value = "couponId",required = false) Long couponId,
                       @RequestParam(value = "useStatus",required = false) Integer useStatus,
                       @RequestParam(value = "orderSn",required = false) String orderSn,
                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        List<SmsCouponHistory> historyList = historyService.list(couponId,useStatus,orderSn,pageSize,pageNum);
        return new CommonResult().pageSuccess(historyList);
    }

    @ApiOperation("领取优惠卷")
    @GetMapping(value = "/pullCoupon")
    public ResultData pullCoupon(@RequestParam Long couponId, @CurrentUser UserCache userCache) {
        com.unifig.mall.bean.domain.CommonResult commonResult = smsCouponHistoryService.pullCoupon(couponId, Long.valueOf(userCache.getUserId()), userCache.getNickName());
        if(commonResult.getCode()==CommonResult.FAILED){
            return ResultData.result(false).setMsg(commonResult.getMessage());
        }
        return ResultData.result(true).setMsg(commonResult.getMessage());
    }

    @ApiOperation("获取店铺优惠券列表")
    @GetMapping(value = "/shopCouponList")
    public RestList<SmsCoupon> shopCouponList(@RequestParam String shopId) {
        List<SmsCoupon> list = historyService.selectShopCouponList(shopId);
        return RestList.resultData(new SmsCoupon()).setData(list).setCount(list.size());
    }


    /**
     * 查询当前登陆用户的优惠券列表
     *
     * @param useStatus
     * @param currentMemberId
     * @return
     */
    @ApiOperation("查询当前登陆用户的优惠券列表")
    @GetMapping(value = "/listCurrentMemberCouponHistory")
    List<SmsCouponHistory> listCurrentMemberCouponHistory(Integer useStatus, Long currentMemberId) {
        List<SmsCouponHistory> smsCouponHistories = smsCouponHistoryService.listCurrentMemberCouponHistory(useStatus, currentMemberId);
        return smsCouponHistories;
    }

    /**
     * 查询当前登陆用户的优惠券剩余数量
     *
     * @return
     */
    @ApiOperation("查询当前登陆用户的优惠券数量")
    @GetMapping(value = "/count")
    Integer couponCount(@CurrentUser UserCache user) {
        List<SmsCouponHistory> smsCouponHistories = smsCouponHistoryService.listCurrentMemberCouponHistory(0,Long.valueOf(user.getUserId()));
        return smsCouponHistories.size();
    }

    /**
     * 根据购物车信息获取可用优惠券
     *
     * @param cartItemList
     * @param type
     * @param currentMemberId
     * @return
     */
    @ApiOperation("根据购物车信息获取可用优惠券")
    @GetMapping(value = "/coupon/listcurrentMemberCarCouponHistoryDetail")
    List<SmsCouponHistoryDetail> listcurrentMemberCarCouponHistoryDetail(List<CartPromotionItem> cartItemList, Integer type, Long currentMemberId) {
        List<SmsCouponHistoryDetail> smsCouponHistoryDetails = smsCouponHistoryService.listcurrentMemberCarCouponHistoryDetail(cartItemList, type, currentMemberId);
        return smsCouponHistoryDetails;
    }


}
