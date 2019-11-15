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
 * 系统用户
 * </p>
 *
 *
 * @since 2019-03-06
 */
@TableName("ums_sys_user")
public class User extends Model<User> {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	/**
	 * 创建者ID
	 */
	@TableField("create_user_id")
	private Long createUserId;
	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;
	/**
	 * 部门ID
	 */
	@TableField("dept_id")
	private Long deptId;

	/**
	 * 部门名称
	 */
	@TableField("dept_name")
	private String deptName;
	/**
	 * 头像
	 */
	private String icon;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 备注信息
	 */
	private String note;
	/**
	 * 最后登录时间
	 */
	@TableField("login_time")
	private Date loginTime;
	/**
	 * 账号名称
	 */
	@TableField("account_name")
	private String accountName;
	/**
	 * 使用人名称
	 */
	@TableField("holder_name")
	private String holderName;
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

	/**
	 * 删除状态(0.删除,1:正常)
	 */
	@TableField("is_delete")
	private String isDelete;

	/**
	 * 角色id
	 */
	@TableField(exist = false)
	private String roleId;

	/**
	 * 角色名称
	 */
	@TableField(exist = false)
	private String roleName;

	@TableField("shop_id")
	private String shopId;

	/**
	 * 第三方平台id
	 */
	@TableField("open_id")
	private String openId;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
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
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", mobile='" + mobile + '\'' +
				", status=" + status +
				", createUserId=" + createUserId +
				", createTime=" + createTime +
				", deptId=" + deptId +
				", deptName='" + deptName + '\'' +
				", icon='" + icon + '\'' +
				", nickname='" + nickname + '\'' +
				", note='" + note + '\'' +
				", loginTime=" + loginTime +
				", accountName='" + accountName + '\'' +
				", holderName='" + holderName + '\'' +
				", updateUserId=" + updateUserId +
				", updateTime=" + updateTime +
				", isDelete='" + isDelete + '\'' +
				", roleId='" + roleId + '\'' +
				", roleName='" + roleName + '\'' +
				", shopId='" + shopId + '\'' +
				", openId='" + openId + '\'' +
				'}';
	}
}
