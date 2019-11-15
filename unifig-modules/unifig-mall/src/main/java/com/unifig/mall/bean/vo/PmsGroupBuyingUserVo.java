/**
 * FileName: PmsGroupBuyingUserVo
 * Author:
 * Date:     2019-09-26 15:05
 * Description: 用户参团列表vo
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.vo;

import com.unifig.mall.bean.model.PmsGroupBuyingUser;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <h3>概要:</h3><p>PmsGroupBuyingUserVo</p>
 * <h3>功能:</h3><p>用户参团列表vo</p>
 *
 * @create 2019-09-26
 * @since 1.0.0
 */
public class PmsGroupBuyingUserVo {

    @ApiModelProperty("团购名称")
    private String name;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("状态 0拼团中  1拼团成功 2 拼团失败")
    private Integer status;

    @ApiModelProperty("发团时间或参团时间")
    private Date createTime;

    @ApiModelProperty("参团人数")
    private Integer number;

    @ApiModelProperty("付款金额")
    private BigDecimal payAmount;

    @ApiModelProperty("商品图片")
    private String productPic;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("过期时间")
    private Date endTime;

    @ApiModelProperty("团购id")
    private String parentId;

    @ApiModelProperty("参团用户列表")
    private List<PmsGroupBuyingUser> userVoList;

    public List<PmsGroupBuyingUser> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<PmsGroupBuyingUser> userVoList) {
        this.userVoList = userVoList;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
