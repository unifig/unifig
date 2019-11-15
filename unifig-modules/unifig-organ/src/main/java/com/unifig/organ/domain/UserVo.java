package com.unifig.organ.domain;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统用户
 * </p>
 *
 *
 * @since 2019-03-06
 */
public class UserVo {

	/**
	 * chax
	 */
	private String searchField;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 使用人名称
	 */
	private String holderName;
	/**
	 * 机构名称
	 */
	private String deptName;

	/**
	 * 当前页
	 */
	private Integer pageNum;

	/**
	 * 页大小
	 */
	private Integer pageSize;

	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

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

	@Override
	public String toString() {
		return "UserVo{" +
				"searchField='" + searchField + '\'' +
				", username='" + username + '\'' +
				", holderName='" + holderName + '\'' +
				", deptName='" + deptName + '\'' +
				", pageNum=" + pageNum +
				", pageSize=" + pageSize +
				'}';
	}
}
