package com.unifig.organ.domain;

import com.baomidou.mybatisplus.annotations.TableField;

import java.util.Date;

/**
 * <p>
 * 部门管理Vo类
 * </p>
 *
 *
 * @since 2019-03-06
 */
public class DeptVo {


	/**
	 * 部门id(机构代码)
	 */
	private Long deptId;
	/**
	 * 上级部门ID(上级机构ID)，一级部门为0
	 */
	private Long parentId;
	/**
	 * 部门名称(机构名称)
	 */
	private String name;
	/**
	 * 排序
	 */
	private Integer orderNum;
	/**
	 * 是否删除  -1：已删除  0：正常
	 */
	private Integer delFlag;
	/**
	 * 机构类型(暂时未定,待产品定好再改备注)
	 */
	private String type;
	/**
	 * 机构状态(0:停用,1:正常)
	 */
	private String status;
	/**
	 * 机构代码
	 */
	private String organizationCode;
	/**
	 * 管理员账号ID
	 */
	private Long userId;
	/**
	 * 管理员账号名称
	 */
	private String userUsername;
	/**
	 * 管理员账号默认密码
	 */
	private String defaultPassword;
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
	private Integer provinceId;
	/**
	 * 省份名称
	 */
	private String provinceName;
	/**
	 * 市ID
	 */
	private Integer cityId;
	/**
	 * 市名称
	 */
	private String cityName;
	/**
	 * 区县ID
	 */
	private Integer areaId;
	/**
	 * 区县名称
	 */
	private String areaName;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 营业执照图片
	 */
	private String businessLicense;
	/**
	 * 营业执照编号
	 */
	private String businessLicenseNumber;
	/**
	 * 创建者ID
	 */
	private Long createUserId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新者ID
	 */
	private Long updateUserId;
	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 当前页
	 */
	private Integer pageNum;

	/**
	 * 页大小
	 */
	private Integer pageSize;

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
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

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
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
	public String toString() {
		return "DeptVo{" +
				"deptId=" + deptId +
				", parentId=" + parentId +
				", name='" + name + '\'' +
				", orderNum=" + orderNum +
				", delFlag=" + delFlag +
				", type='" + type + '\'' +
				", status='" + status + '\'' +
				", organizationCode='" + organizationCode + '\'' +
				", userId=" + userId +
				", userUsername='" + userUsername + '\'' +
				", defaultPassword='" + defaultPassword + '\'' +
				", contacts='" + contacts + '\'' +
				", phone='" + phone + '\'' +
				", logo='" + logo + '\'' +
				", synopsis='" + synopsis + '\'' +
				", provinceId=" + provinceId +
				", provinceName='" + provinceName + '\'' +
				", cityId=" + cityId +
				", cityName='" + cityName + '\'' +
				", areaId=" + areaId +
				", areaName='" + areaName + '\'' +
				", address='" + address + '\'' +
				", businessLicense='" + businessLicense + '\'' +
				", businessLicenseNumber='" + businessLicenseNumber + '\'' +
				", createUserId=" + createUserId +
				", createTime=" + createTime +
				", updateUserId=" + updateUserId +
				", updateTime=" + updateTime +
				", pageNum=" + pageNum +
				", pageSize=" + pageSize +
				'}';
	}
}
