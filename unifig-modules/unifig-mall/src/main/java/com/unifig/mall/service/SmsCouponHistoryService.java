package com.unifig.mall.service;

import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.bean.vo.CouponVo;
import com.unifig.mall.bean.vo.SmsCouponHistoryVo;
import com.unifig.model.CartPromotionItem;
import com.unifig.model.SmsCoupon;
import com.unifig.model.SmsCouponHistory;
import com.unifig.model.SmsCouponHistoryDetail;
import com.unifig.result.ResultData;

import java.util.List;

/**
 * 优惠券领取记录管理Service
 *    on 2018/11/6.
 */
public interface SmsCouponHistoryService {
    /**
     * 分页查询优惠券领取记录
     *
     * @param couponId  优惠券id
     * @param useStatus 使用状态
     * @param orderSn   使用订单号码
     */
    List<SmsCouponHistory> list(Long couponId, Integer useStatus, String orderSn, Integer pageSize, Integer pageNum);

    /**
     * 查询当前登陆用户的优惠券列表
     *
     * @param useStatus
     * @param currentMemberId
     * @return
     */
    List<SmsCouponHistory> listCurrentMemberCouponHistory(Integer useStatus, Long currentMemberId);

    /**
     * 会员添加优惠券
     *
     * @param couponId
     * @param currentMemberId
     * @param nikeName
     * @return
     */
    CommonResult pullCoupon(Long couponId, Long currentMemberId, String nikeName);

    /**
     * 根据购物车信息获取可用优惠券
     *
     * @param cartItemList
     * @param type
     * @param currentMemberId
     * @return
     */
    List<SmsCouponHistoryDetail> listcurrentMemberCarCouponHistoryDetail(List<CartPromotionItem> cartItemList, Integer type, Long currentMemberId);

    /**
     * 核销
     * @param code
     * @param userId
     * @return
     */
    ResultData verify(String code, String userId);

    /**
     * 查看用户优惠卷列表
     * @param status
     * @return
     */
    List<SmsCouponHistoryVo> selectUserCouponList(String userId,Integer status);

    /**
     * 查看优惠券详情
     * @param userId
     * @param id
     * @return
     */
    SmsCouponHistoryVo selectById(String userId, Integer id);

    /**
     * 后台发放优惠券
     * @param couponId
     * @param currentMemberIds
     * @return
     */
    ResultData giveOut(Long couponId, List<Long> currentMemberIds);

    SmsCouponHistoryVo selectVerify(String code, String userId);

    /**
     * 获取店铺优惠券列表
     * @param shopId
     * @return
     */
    List<SmsCoupon> selectShopCouponList(String shopId);

    ResultData giveOutList(CouponVo vo);
}
