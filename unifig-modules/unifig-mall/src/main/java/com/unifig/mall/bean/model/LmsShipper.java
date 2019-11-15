package com.unifig.mall.bean.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 物流订单表
 * </p>
 *
 *
 * @since 2019-02-15
 */
@TableName("lms_shipper")
public class LmsShipper extends Model<LmsShipper> {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 快递公司编码
     */
    @TableField("shipper_code")
    private String shipperCode;
    /**
     * 快递公司名称
     */
    @TableField("shipper_name")
    private String shipperName;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 上传时间
     */
    @TableField("upload_time")
    private Date uploadTime;
    /**
     * 删记录时间
     */
    @TableField("edit_time")
    private Date editTime;
    /**
     * space domain
     */
    @TableField("ratel_no")
    private String ratelNo;
    /**
     * 1可以 0不可以
     */
    private Integer enable;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getRatelNo() {
        return ratelNo;
    }

    public void setRatelNo(String ratelNo) {
        this.ratelNo = ratelNo;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "LmsShipper{" +
        ", id=" + id +
        ", shipperCode=" + shipperCode +
        ", shipperName=" + shipperName +
        ", createTime=" + createTime +
        ", uploadTime=" + uploadTime +
        ", editTime=" + editTime +
        ", ratelNo=" + ratelNo +
        ", enable=" + enable +
        "}";
    }
}
