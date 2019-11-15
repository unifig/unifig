package com.unifig.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.unifig.mall.bean.vo.CouponUserVo;
import com.unifig.mall.bean.vo.CouponVo;
import com.unifig.mall.bean.vo.SmsCouponHistoryVo;
import com.unifig.mall.dao.SmsCouponHistoryDao;
import com.unifig.mall.bean.domain.CommonResult;
import com.unifig.mall.mapper.SmsCouponHistoryMapper;
import com.unifig.mall.mapper.SmsCouponMapper;
import com.unifig.mall.bean.model.SmsCouponHistoryExample;
import com.unifig.mall.service.SmsCouponHistoryService;
import com.unifig.model.*;
import com.unifig.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 优惠券领取记录管理Service实现类
 *    on 2018/11/6.
 */
@Service
public class SmsCouponHistoryServiceImpl implements SmsCouponHistoryService {
    @Autowired
    private SmsCouponHistoryMapper historyMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;
    @Autowired
    private SmsCouponMapper couponMapper;

    @Override
    public List<SmsCouponHistory> list(Long couponId, Integer useStatus, String orderSn, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.setOrderByClause("id DESC");
        SmsCouponHistoryExample.Criteria criteria = example.createCriteria();
        if (couponId != null) {
            criteria.andCouponIdEqualTo(couponId);
        }
        if (useStatus != null) {
            criteria.andUseStatusEqualTo(useStatus);
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        return historyMapper.selectByExample(example);
    }

    @Override
    public List<SmsCouponHistory> listCurrentMemberCouponHistory(Integer useStatus, Long currentMemberId) {
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = couponHistoryExample.createCriteria();
        criteria.andMemberIdEqualTo(currentMemberId);
        if (useStatus != null) {
            criteria.andUseStatusEqualTo(useStatus);
        }
        return couponHistoryMapper.selectByExample(couponHistoryExample);
    }


    @Override
    public CommonResult pullCoupon(Long couponId, Long currentMemberId, String nikeName) {
        //获取优惠券信息，判断数量
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            return new CommonResult().failed("优惠券不存在");
        }
        if (coupon.getCount() <= 0) {
            return new CommonResult().failed("优惠券已经领完了");
        }
        Date now = new Date();
//        if (now.before(coupon.getEnableTime())) {
//            return new CommonResult().failed("优惠券还没到领取时间");
//        }
        //判断用户领取的优惠券数量是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(currentMemberId);
        int count = couponHistoryMapper.countByExample(couponHistoryExample);
        if (count >= coupon.getPerLimit()) {
            return new CommonResult().failed("您已经领取过该优惠券");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(currentMemberId));
        couponHistory.setCreateTime(now);
        couponHistory.setMemberId(currentMemberId);
        couponHistory.setMemberNickname(nikeName);
        //主动领取
        couponHistory.setGetType(1);
        //未使用
        couponHistory.setUseStatus(0);
        //生成唯一编码
        couponHistory.setCode(verifyCode());
        couponHistoryMapper.insert(couponHistory);
        //修改优惠券表的数量、领取数量
        coupon.setCount(coupon.getCount() - 1);
        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + 1);
        couponMapper.updateByPrimaryKey(coupon);
        return new CommonResult().success("领取成功", null);
    }


    @Override
    public List<SmsCouponHistoryDetail> listcurrentMemberCarCouponHistoryDetail(List<CartPromotionItem> cartItemList, Integer type, Long currentMemberId) {
        Date now = new Date();
        //获取该用户所有优惠券
        List<SmsCouponHistoryDetail> allList = couponHistoryDao.getDetailList(currentMemberId);
        //根据优惠券使用类型来判断优惠券是否可用
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();
        for (SmsCouponHistoryDetail couponHistoryDetail : allList) {
            Integer useType = couponHistoryDetail.getCoupon().getUseType();
            BigDecimal minPoint = couponHistoryDetail.getCoupon().getMinPoint();
            Date endTime = couponHistoryDetail.getCoupon().getEndTime();
            if (useType.equals(0)) {
                //0->全场通用
                //判断是否满足优惠起点
                //计算购物车商品的总价
                BigDecimal totalAmount = calcTotalAmount(cartItemList);
                if (now.before(endTime) && totalAmount.subtract(minPoint).intValue() >= 0) {
                    enableList.add(couponHistoryDetail);
                } else {
                    disableList.add(couponHistoryDetail);
                }
            } else if (useType.equals(1)) {
                //1->指定分类
                //计算指定分类商品的总价
                List<Long> productCategoryIds = new ArrayList<>();
                for (SmsCouponProductCategoryRelation categoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                    productCategoryIds.add(categoryRelation.getProductCategoryId());
                }
                BigDecimal totalAmount = calcTotalAmountByproductCategoryId(cartItemList, productCategoryIds);
                if (now.before(endTime) && totalAmount.intValue() > 0 && totalAmount.subtract(minPoint).intValue() >= 0) {
                    enableList.add(couponHistoryDetail);
                } else {
                    disableList.add(couponHistoryDetail);
                }
            } else if (useType.equals(2)) {
                //2->指定商品
                //计算指定商品的总价
                List<Long> productIds = new ArrayList<>();
                for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                    productIds.add(productRelation.getProductId());
                }
                BigDecimal totalAmount = calcTotalAmountByProductId(cartItemList, productIds);
                if (now.before(endTime) && totalAmount.intValue() > 0 && totalAmount.subtract(minPoint).intValue() >= 0) {
                    enableList.add(couponHistoryDetail);
                } else {
                    disableList.add(couponHistoryDetail);
                }
            }
        }
        if (type.equals(1)) {
            return enableList;
        } else {
            return disableList;
        }
    }

    @Override
    public ResultData verify(String code, String userId) {
        //获取商家可核实优惠券id集合
        List<String> couponList = historyMapper.selectShopVerifyCouponList(userId);

        //获取单前code优惠券信息
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = example.createCriteria();
        criteria.andUseStatusEqualTo(0);
        criteria.andCodeEqualTo(code);
        List<SmsCouponHistory> smsCouponHistories = historyMapper.selectByExample(example);
        if(smsCouponHistories.size()<0){
            return ResultData.result(false).setMsg("优惠卷不存在,或者优惠券已过期");
        }
        SmsCouponHistory smsCouponHistory = smsCouponHistories.get(0);
        if(!couponList.contains(smsCouponHistory.getCouponId().toString())){
            return ResultData.result(false).setMsg("非本店优惠券,请重新核实");
        }
        //修改优惠券使用数量
        SmsCoupon smsCoupon = couponMapper.selectByPrimaryKey(smsCouponHistory.getCouponId());
        smsCoupon.setUseCount(smsCoupon.getUseCount()+1);
        couponMapper.updateByPrimaryKeySelective(smsCoupon);

        //修改用户优惠券状态
        smsCouponHistory.setUseStatus(1);
        smsCouponHistory.setUseTime(new Date());
        historyMapper.updateByPrimaryKeySelective(smsCouponHistory);
        return ResultData.result(true).setMsg("核销成功");
    }

    @Override
    public List<SmsCouponHistoryVo> selectUserCouponList(String userId,Integer status) {
        return historyMapper.selectUserCouponList(userId,status);
    }

    @Override
    public SmsCouponHistoryVo selectById(String userId, Integer id) {
        //只查询未使用详情
        List<SmsCouponHistoryVo> smsCouponHistoryVos = historyMapper.selectUserCouponList(userId, 0);
        List<SmsCouponHistoryVo> collect = smsCouponHistoryVos.stream().filter(li -> li.getId().toString().equals(id.toString())).collect(Collectors.toList());
        return collect.size() >0 ? collect.get(0): null;
    }

    @Override
    public ResultData giveOut(Long couponId, List<Long> currentMemberIds) {
        //获取优惠券信息，判断数量
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null) {
            return ResultData.result(false).setMsg("优惠券不存在");
        }
        if (coupon.getCount() <= 0) {
            return  ResultData.result(false).setMsg("优惠券发放完了");
        }
        if (coupon.getCount() < currentMemberIds.size()) {
            return  ResultData.result(false).setMsg("发放数量大于剩余数量");
        }
        Date now = new Date();

        for (Long userId: currentMemberIds){
            //判断用户领取的优惠券数量是否超过限制
            SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
            couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(userId);
            int count = couponHistoryMapper.countByExample(couponHistoryExample);
            //生成领取优惠券历史
            SmsCouponHistory couponHistory = new SmsCouponHistory();
            couponHistory.setCouponId(couponId);
            couponHistory.setCouponCode(generateCouponCode(userId));
            couponHistory.setCreateTime(now);
            couponHistory.setMemberId(userId);
            //主动领取
            couponHistory.setGetType(0);
            //未使用
            couponHistory.setUseStatus(0);
            //生成唯一编码
            couponHistory.setCode(verifyCode());
            couponHistoryMapper.insert(couponHistory);
            //修改优惠券表的数量、领取数量
        }
        //更改优惠券信息
        coupon.setCount(coupon.getCount() - currentMemberIds.size());
        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + currentMemberIds.size());
//        couponMapper.updateByPrimaryKey(coupon);
        couponMapper.updateByPrimaryKeySelective(coupon);
        return ResultData.result(true).setMsg("发放成功");
    }

    @Override
    public SmsCouponHistoryVo selectVerify(String code, String userId) {
        return couponHistoryMapper.selectVerify(code,userId);
    }

    @Override
    public List<SmsCoupon> selectShopCouponList(String shopId) {
        return couponMapper.selectShopCouponList(shopId);
    }

    @Override
    public ResultData giveOutList(CouponVo vo) {
        Long couponId = vo.getCouponId();
        //获取优惠券信息，判断数量
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(vo.getCouponId());
        if (coupon == null) {
            return ResultData.result(false).setMsg("优惠券不存在");
        }
        if (coupon.getCount() <= 0) {
            return  ResultData.result(false).setMsg("优惠券发放完了");
        }
        if (coupon.getCount() < vo.getUsers().size()) {
            return  ResultData.result(false).setMsg("发放数量大于剩余数量");
        }
        Date now = new Date();

        for (CouponUserVo user: vo.getUsers()){
            Long userId = user.getCurrentMemberId();
            //判断用户领取的优惠券数量是否超过限制
            SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
            couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(userId);
            int count = couponHistoryMapper.countByExample(couponHistoryExample);
            //生成领取优惠券历史
            SmsCouponHistory couponHistory = new SmsCouponHistory();
            couponHistory.setCouponId(couponId);
            couponHistory.setMemberNickname(user.getCurrentMemberName());
            couponHistory.setCouponCode(generateCouponCode(userId));
            couponHistory.setCreateTime(now);
            couponHistory.setMemberId(userId);
            //主动领取
            couponHistory.setGetType(0);
            //未使用
            couponHistory.setUseStatus(0);
            //生成唯一编码
            couponHistory.setCode(verifyCode());
            couponHistoryMapper.insert(couponHistory);
            //修改优惠券表的数量、领取数量
        }
        //更改优惠券信息
        coupon.setCount(coupon.getCount() - vo.getUsers().size());
        coupon.setReceiveCount(coupon.getReceiveCount() == null ? 1 : coupon.getReceiveCount() + vo.getUsers().size());
//        couponMapper.updateByPrimaryKey(coupon);
        couponMapper.updateByPrimaryKeySelective(coupon);
        return ResultData.result(true).setMsg("发放成功");
    }

    private BigDecimal calcTotalAmount(List<CartPromotionItem> cartItemList) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
            total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByproductCategoryId(List<CartPromotionItem> cartItemList, List<Long> productCategoryIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if (productCategoryIds.contains(item.getProductCategoryId())) {
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

    private BigDecimal calcTotalAmountByProductId(List<CartPromotionItem> cartItemList, List<Long> productIds) {
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : cartItemList) {
            if (productIds.contains(item.getProductId())) {
                BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
                total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }

    /**
     * 16位优惠码生成：时间戳后8位+4位随机数+用户id后4位
     */
    private String generateCouponCode(Long memberId) {
        StringBuilder sb = new StringBuilder();
        Long currentTimeMillis = System.currentTimeMillis();
        String timeMillisStr = currentTimeMillis.toString();
        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
        for (int i = 0; i < 4; i++) {
            sb.append(new Random().nextInt(10));
        }
        String memberIdStr = memberId.toString();
        if (memberIdStr.length() <= 4) {
            sb.append(String.format("%04d", memberId));
        } else {
            sb.append(memberIdStr.substring(memberIdStr.length() - 4));
        }
        return sb.toString();
    }

    /**
     * /**
     * 包含了二十六个字母和十个数字的字符数组
     */
    public static char[] charArray(){
        int i = 1234567890;
        String s ="qwertyuiopasdfghjklzxcvbnm";
        String S=s.toUpperCase();
        String word=s+S+i;
        char[] c=word.toCharArray();
        return c;
    }

    /**
     * 生成六位核销码
     * @return
     */
    public static String verifyCode(){
        char[] c= charArray();//获取包含26个字母大小写和数字的字符数组
        Random rd = new Random();
        String code="";
        for (int k = 0; k < 6; k++) {
            int index = rd.nextInt(c.length);//随机获取数组长度作为索引
            code+=c[index];//循环添加到字符串后面
        }
        return code;
    }
}
