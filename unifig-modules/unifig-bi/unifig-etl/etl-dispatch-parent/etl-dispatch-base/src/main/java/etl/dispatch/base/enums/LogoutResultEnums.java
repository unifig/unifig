package etl.dispatch.base.enums;

import org.springframework.util.StringUtils;

/**
 * 登录验证结果枚举类（登录结果描述信息可从属性文件中获取，实现国际化）
 */
public enum LogoutResultEnums {
	LOGOUT_SUCCESS(100, "注销成功"), // 登录成功
	LOGOUT_FAIL_EXCEPTION(-101, "注销发生异常，请联系管理员"),
	LOGOUT_FAIL_TOKEN(-102, "注销失败,Token令牌为空");

	private int code;

	private String msg;

	private String exception;

	public String getException() {
		return exception;
	}

	public LogoutResultEnums setException(String exception) {
		this.exception = exception;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	LogoutResultEnums(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		if (StringUtils.hasLength(exception)) {
			return this.getMsg() + ",异常信息:[" + this.getException() + "]";
		}
		return this.getMsg();
	}
}
