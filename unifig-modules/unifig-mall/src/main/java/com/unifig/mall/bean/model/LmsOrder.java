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
@TableName("lms_order")
public class LmsOrder extends Model<LmsOrder> {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 供应商id
     */
    @TableField("supplier_id")
    private Integer supplierId;
    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;
    /**
     * 供应商地址id
     */
    @TableField("supplier_address_id")
    private String supplierAddressId;
    /**
     * 收货人地址id
     */
    @TableField("consignee_address_id")
    private String consigneeAddressId;
    /**
     * 收货人名称
     */
    @TableField("consignee_address_name")
    private String consigneeAddressName;
    /**
     * 物流订单sn
     */
    @TableField("logistics_sn")
    private String logisticsSn;
    /**
     * 商城订单sn
     */
    @TableField("mall_sn")
    private String mallSn;
    /**
     * 物流公司sn
     */
    @TableField("shipper_logistic_sn")
    private String shipperLogisticSn;
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
     * 上传时间
     */
    @TableField("upload_time")
    private Date uploadTime;
    /**
     * 快递鸟商户id
     */
    private String EBusinessID;
    /**
     * 80773 运输中 80774 运输异常  80991 已完成  
     */
    private Integer status;
    /**
     * 新增记录时间
     */
    @TableField("create_time")
    private Date createTime;
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
    @TableField("shipper_print_template")
    private String shipperPrintTemplate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierAddressId() {
        return supplierAddressId;
    }

    public void setSupplierAddressId(String supplierAddressId) {
        this.supplierAddressId = supplierAddressId;
    }

    public String getConsigneeAddressId() {
        return consigneeAddressId;
    }

    public void setConsigneeAddressId(String consigneeAddressId) {
        this.consigneeAddressId = consigneeAddressId;
    }

    public String getConsigneeAddressName() {
        return consigneeAddressName;
    }

    public void setConsigneeAddressName(String consigneeAddressName) {
        this.consigneeAddressName = consigneeAddressName;
    }

    public String getLogisticsSn() {
        return logisticsSn;
    }

    public void setLogisticsSn(String logisticsSn) {
        this.logisticsSn = logisticsSn;
    }

    public String getMallSn() {
        return mallSn;
    }

    public void setMallSn(String mallSn) {
        this.mallSn = mallSn;
    }

    public String getShipperLogisticSn() {
        return shipperLogisticSn;
    }

    public void setShipperLogisticSn(String shipperLogisticSn) {
        this.shipperLogisticSn = shipperLogisticSn;
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

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
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

    public String getShipperPrintTemplate() {
        return shipperPrintTemplate;
    }

    public void setShipperPrintTemplate(String shipperPrintTemplate) {
        this.shipperPrintTemplate = shipperPrintTemplate;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "LmsOrder{" +
        ", id=" + id +
        ", supplierId=" + supplierId +
        ", supplierName=" + supplierName +
        ", supplierAddressId=" + supplierAddressId +
        ", consigneeAddressId=" + consigneeAddressId +
        ", consigneeAddressName=" + consigneeAddressName +
        ", logisticsSn=" + logisticsSn +
        ", mallSn=" + mallSn +
        ", shipperLogisticSn=" + shipperLogisticSn +
        ", shipperCode=" + shipperCode +
        ", shipperName=" + shipperName +
        ", uploadTime=" + uploadTime +
        ", EBusinessID=" + EBusinessID +
        ", status=" + status +
        ", createTime=" + createTime +
        ", editTime=" + editTime +
        ", ratelNo=" + ratelNo +
        ", enable=" + enable +
        ", shipperPrintTemplate=" + shipperPrintTemplate +
        "}";
    }
}
