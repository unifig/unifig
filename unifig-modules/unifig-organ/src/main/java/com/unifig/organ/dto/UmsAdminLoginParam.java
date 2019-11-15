package com.unifig.organ.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 用户登录参数
 *    on 2018/4/26.
 */
public class UmsAdminLoginParam {
	@ApiModelProperty(value = "用户名", required = true)
	private String username;
	@ApiModelProperty(value = "密码", required = true)
	private String password;
	@ApiModelProperty(value = "密码类型")
	private String passwordType;

	public String getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
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
}
