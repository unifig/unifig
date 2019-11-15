package com.unifig.organ.controller;

import com.unifig.annotation.CurrentUser;
import com.unifig.entity.cache.UserCache;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCouponHistory;
import com.unifig.model.SmsCouponHistoryDetail;
import com.unifig.model.UmsMember;
import com.unifig.organ.domain.CommonResult;
import com.unifig.organ.feign.SandCartFeignClient;
import com.unifig.organ.feign.SandCouponFeignClient;
import com.unifig.organ.service.UmsMemberCouponService;
import com.unifig.organ.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 用户优惠券管理Controller
 *    on 2018/8/29.
 */
@Controller
@Api(tags = "UmsMemberCouponController", description = "用户优惠券管理")
@RequestMapping("/member/coupon")
@ApiIgnore
public class UmsMemberCouponController {
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private SandCartFeignClient unifigCartFeignClient;
    @Autowired
    private SandCouponFeignClient unifigCouponFeignClient;

    @ApiOperation("领取指定优惠券")
    @RequestMapping(value = "/add/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    public Object add(@PathVariable Long couponId, @CurrentUser UserCache userCache) {
        return unifigCouponFeignClient.pullCoupon(couponId, Long.valueOf( userCache.getUserId()), userCache.getNickName());
    }

    @ApiOperation("获取用户优惠券列表")
    @ApiImplicitParam(name = "useStatus", value = "优惠券筛选类型:0->未使用；1->已使用；2->已过期",
            allowableValues = "0,1,2", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list(@RequestParam(value = "useStatus", required = false) Integer useStatus, @CurrentUser UserCache userCache) {
        List<SmsCouponHistory> couponHistoryList = unifigCouponFeignClient.listCurrentMemberCouponHistory(useStatus, Long.valueOf( userCache.getUserId()));
        return new CommonResult().success(couponHistoryList);
    }

    @ApiOperation("获取登录会员购物车的相关优惠券")
    @ApiImplicitParam(name = "type", value = "使用可用:0->不可用；1->可用",
            defaultValue = "1", allowableValues = "0,1", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/list/cart/{type}", method = RequestMethod.GET)
    @ResponseBody
    public Object listCart(@PathVariable Integer type,@CurrentUser UserCache userCache) {
        List<CartPromotionItem> cartPromotionItemList = unifigCartFeignClient.listPromotion(Long.valueOf( userCache.getUserId()));
        List<SmsCouponHistoryDetail> couponHistoryList = unifigCouponFeignClient.listcurrentMemberCarCouponHistoryDetail(cartPromotionItemList, type, Long.valueOf( userCache.getUserId()));
        return new CommonResult().success(couponHistoryList);
    }
}
