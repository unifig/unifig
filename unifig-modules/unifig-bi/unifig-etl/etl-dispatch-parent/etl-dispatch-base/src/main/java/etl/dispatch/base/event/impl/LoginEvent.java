package etl.dispatch.base.event.impl;

import java.util.Date;

import etl.dispatch.base.enums.LoginAuthResultEnums;
import etl.dispatch.base.enums.LoginTypeEnum;
import etl.dispatch.base.event.AbstractWebVisitEvent;

/**
 * @Title:登录注销事件
 *
 */

public class LoginEvent extends AbstractWebVisitEvent {
	private LoginTypeEnum loginType;
	private LoginAuthResultEnums loginResult;
	private Date loginTime;
	private static final long serialVersionUID = 5009199522943324769L;

	public LoginEvent(Object source) {
		super(source);
	}

	public LoginTypeEnum getLoginType() {
		return loginType;
	}

	public void setLoginType(LoginTypeEnum loginType) {
		this.loginType = loginType;
	}

	public LoginAuthResultEnums getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(LoginAuthResultEnums loginResult) {
		this.loginResult = loginResult;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public LoginEvent(Object source, LoginTypeEnum loginType, Date loginTime) {
        super(source);
        this.loginType = loginType;
        this.loginTime = loginTime;
    }
	
	public LoginEvent(Object source, LoginTypeEnum loginType, Date loginTime, LoginAuthResultEnums loginResult) {
		super(source);
		this.loginType = loginType;
		this.loginTime = loginTime;
		this.loginResult = loginResult;
	}
}
