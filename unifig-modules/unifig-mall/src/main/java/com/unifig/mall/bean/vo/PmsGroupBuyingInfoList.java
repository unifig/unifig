/**
 * FileName: PmsGroupBuyingInfoList
 * Author:
 * Date:     2019-09-27 15:08
 * Description: 参团列表vo
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.unifig.mall.bean.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * <h3>概要:</h3><p>PmsGroupBuyingInfoList</p>
 * <h3>功能:</h3><p>参团列表vo</p>
 *
 * @create 2019-09-27
 * @since 1.0.0
 */
public class PmsGroupBuyingInfoList {

    @ApiModelProperty("团购id")
    private String id;

    @ApiModelProperty("团购名称")
    private String name;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("参团人数")
    private Integer number ;

    @ApiModelProperty("状态  0拼团中  1拼团成功 2 拼团失败")
    private Integer status;

    @ApiModelProperty("发团时间")
    private Date createTime;

    @ApiModelProperty("发团人")
    private String userName;

    @ApiModelProperty("成团人数")
    private Integer successNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSuccessNumber() {
        return successNumber;
    }

    public void setSuccessNumber(Integer successNumber) {
        this.successNumber = successNumber;
    }
}
