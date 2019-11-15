package com.unifig.organ.feign;

import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCouponHistory;
import com.unifig.model.SmsCouponHistoryDetail;
import com.unifig.organ.domain.CommonResult;
import io.swagger.models.auth.In;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "unifig-mall")
public interface SandCouponFeignClient {

    @PostMapping(value = "/online/upsert")
    void upsertOnline(@RequestParam(name = "uuid") String uuid,
                      @RequestParam(name = "token") String token,
                      @RequestParam(name = "uid") String uid,
                      @RequestParam(name = "online") String online);

    @GetMapping(value = "/client/coupon/pullCoupon")
    CommonResult pullCoupon(@RequestParam("couponId") Long couponId,@RequestParam("currentMemberId") Long currentMemberId,@RequestParam("nikeName") String nikeName);


    @GetMapping(value = "/client/coupon/count")
    Integer couponCount();

    /**
     * 查询当前登陆用户的优惠券列表
     *
     * @param useStatus
     * @param currentMemberId
     * @return
     */
    @GetMapping(value = "/coupon/listCurrentMemberCouponHistory")
    List<SmsCouponHistory> listCurrentMemberCouponHistory(@RequestParam("useStatus") Integer useStatus,@RequestParam("currentMemberId") Long currentMemberId);

    /**
     * 根据购物车信息获取可用优惠券
     *
     * @param cartItemList
     * @param type
     * @param currentMemberId
     * @return
     */
    @GetMapping(value = "/coupon/listcurrentMemberCarCouponHistoryDetail")
    List<SmsCouponHistoryDetail> listcurrentMemberCarCouponHistoryDetail(@RequestParam("cartItemList") List<CartPromotionItem> cartItemList,@RequestParam("type") Integer type,@RequestParam("currentMemberId") Long currentMemberId);

}
