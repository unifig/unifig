package com.unifig.mall.bean.domain;

import com.unifig.model.OmsCartItem;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 生成订单时传入的参数
 *    on 2018/8/30.
 */
public class OrderParam {
    //收货地址id
    @ApiModelProperty("收货地址id")
    private Long memberReceiveAddressId;
    //优惠券id
    @ApiModelProperty("优惠券id")
    private Long couponId;
    //使用的积分数
    @ApiModelProperty("使用的积分数")
    private Integer useIntegration;
    //支付方式
    @ApiModelProperty("支付方式")
    private Integer payType =2;

    //购物车id
    @ApiModelProperty("购物车ids")
    private List<Long> ids;

    /**
     * 购买商品信息
     */
    @ApiModelProperty("购买商品信息")
    private OmsCartItem omsCartItem;

    /**
     * 团购id
     */
    @ApiModelProperty("团购id")
    private String groupBuyingInfoId;

    public String getGroupBuyingInfoId() {
        return groupBuyingInfoId;
    }

    public void setGroupBuyingInfoId(String groupBuyingInfoId) {
        this.groupBuyingInfoId = groupBuyingInfoId;
    }

    public OmsCartItem getOmsCartItem() {
        return omsCartItem;
    }

    public void setOmsCartItem(OmsCartItem omsCartItem) {
        this.omsCartItem = omsCartItem;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getMemberReceiveAddressId() {
        return memberReceiveAddressId;
    }

    public void setMemberReceiveAddressId(Long memberReceiveAddressId) {
        this.memberReceiveAddressId = memberReceiveAddressId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Integer useIntegration) {
        this.useIntegration = useIntegration;
    }
}
