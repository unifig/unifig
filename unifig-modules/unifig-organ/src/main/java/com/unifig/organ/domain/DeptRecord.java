package com.unifig.organ.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 部门管理操作记录表
 * </p>
 *
 *
 * @since 2019-03-06
 */
@TableName("ums_sys_dept_record")
public class DeptRecord extends Model<DeptRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 部门id(机构代码)
     */
    @TableField("dept_id")
    private Long deptId;
    /**
     * 上级部门名称
     */
    @TableField("parent_name")
    private String parentName;
    /**
     * 上级部门ID(上级机构ID)，一级部门为0
     */
    @TableField("parent_id")
    private Long parentId;
    /**
     * 部门名称(机构名称)
     */
    private String name;
    /**
     * 管理员账号ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 管理员账号名称
     */
    @TableField("user_username")
    private String userUsername;
    /**
     * 操作状态(0:停用,1:正常2:修改密码3:创建)
     */
    @TableField("operate_status")
    private String operateStatus;
    /**
     * 机构联系人
     */
    private String contacts;
    /**
     * 电话
     */
    private String phone;
    /**
     * 机构LOGO图
     */
    private String logo;
    /**
     * 机构简介
     */
    private String synopsis;
    /**
     * 省份ID
     */
    @TableField("province_id")
    private Integer provinceId;
    /**
     * 省份名称
     */
    @TableField("province_name")
    private String provinceName;
    /**
     * 市ID
     */
    @TableField("city_id")
    private Integer cityId;
    /**
     * 市名称
     */
    @TableField("city_name")
    private String cityName;
    /**
     * 区县ID
     */
    @TableField("area_id")
    private Integer areaId;
    /**
     * 区县名称
     */
    @TableField("area_name")
    private String areaName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 营业执照图片
     */
    @TableField("business_license")
    private String businessLicense;
    /**
     * 营业执照编号
     */
    @TableField("business_license_number")
    private String businessLicenseNumber;
    /**
     * 创建者ID
     */
    @TableField("create_user_id")
    private Long createUserId;
    /**
     * 创建时间(操作时间)
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新者ID
     */
    @TableField("update_user_id")
    private Long updateUserId;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(String operateStatus) {
        this.operateStatus = operateStatus;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getBusinessLicenseNumber() {
        return businessLicenseNumber;
    }

    public void setBusinessLicenseNumber(String businessLicenseNumber) {
        this.businessLicenseNumber = businessLicenseNumber;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DeptRecord{" +
        ", id=" + id +
        ", deptId=" + deptId +
        ", parentName=" + parentName +
        ", parentId=" + parentId +
        ", name=" + name +
        ", userId=" + userId +
        ", userUsername=" + userUsername +
        ", operateStatus=" + operateStatus +
        ", contacts=" + contacts +
        ", phone=" + phone +
        ", logo=" + logo +
        ", synopsis=" + synopsis +
        ", provinceId=" + provinceId +
        ", provinceName=" + provinceName +
        ", cityId=" + cityId +
        ", cityName=" + cityName +
        ", areaId=" + areaId +
        ", areaName=" + areaName +
        ", address=" + address +
        ", businessLicense=" + businessLicense +
        ", businessLicenseNumber=" + businessLicenseNumber +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        "}";
    }
}
