package com.unifig.mall.controller;

import com.unifig.mall.bean.dto.CommonResult;
import com.unifig.mall.bean.dto.SmsCouponParam;
import com.unifig.mall.bean.vo.CouponVo;
import com.unifig.mall.service.SmsCouponHistoryService;
import com.unifig.mall.service.SmsCouponService;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCoupon;
import com.unifig.model.SmsCouponHistory;
import com.unifig.model.SmsCouponHistoryDetail;
import com.unifig.result.ResultData;
import io.swagger.annotations.*;
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
@RequestMapping("/coupon")
@ApiIgnore
public class SmsCouponController {
    @Autowired
    private SmsCouponService couponService;

    @Autowired
    private SmsCouponHistoryService smsCouponHistoryService;

    @ApiOperation("添加优惠券")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Object add(@RequestBody SmsCouponParam couponParam) {
        int count = couponService.create(couponParam);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("删除优惠券")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@PathVariable Long id) {
        int count = couponService.delete(id);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("修改优惠券")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Object update(@PathVariable Long id, @RequestBody SmsCouponParam couponParam) {
        int count = couponService.update(id, couponParam);
        if (count > 0) {
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }

    @ApiOperation("根据优惠券名称和类型分页获取优惠券列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Object list(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<SmsCoupon> couponList = couponService.list(name, type, pageSize, pageNum);
        return new CommonResult().pageSuccess(couponList);
    }

    @ApiOperation("获取单个优惠券的详细信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object getItem(@PathVariable Long id) {
        SmsCouponParam couponParam = couponService.getItem(id);
        return new CommonResult().success(couponParam);
    }

    @ApiOperation("领取优惠卷")
    @GetMapping(value = "/pullCoupon")
    public CommonResult pullCoupon(@RequestParam Long couponId, @RequestParam Long currentMemberId, @RequestParam String nikeName) {
        smsCouponHistoryService.pullCoupon(couponId, currentMemberId, nikeName);
        return null;
    }


//    @ApiOperation("发放优惠卷")
//    @PostMapping(value = "/giveOut")
//    public ResultData giveOut(@ApiParam("优惠券id") @RequestParam(value = "couponId",required = true) Long couponId,
//                              @ApiParam("用户id") @RequestParam(value = "currentMemberIds",required = true) List<Long> currentMemberIds) {
//        return smsCouponHistoryService.giveOut(couponId,currentMemberIds);
//    }

    @ApiOperation("发放优惠卷")
    @PostMapping(value = "/giveOut")
    public ResultData giveOut(@RequestBody CouponVo vo) {
        return smsCouponHistoryService.giveOutList(vo);
    }


    /**
     * 查询当前登陆用户的优惠券列表
     *
     * @param useStatus
     * @param currentMemberId
     * @return
     */
    @ApiOperation("查询当前登陆用户的优惠券列表")
    @GetMapping(value = "/coupon/listCurrentMemberCouponHistory")
    List<SmsCouponHistory> listCurrentMemberCouponHistory(Integer useStatus, Long currentMemberId) {
        List<SmsCouponHistory> smsCouponHistories = smsCouponHistoryService.listCurrentMemberCouponHistory(useStatus, currentMemberId);
        return smsCouponHistories;
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
