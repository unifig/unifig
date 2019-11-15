package etl.dispatch.base.enums;

import org.springframework.util.StringUtils;

/**
 * 登录验证结果枚举类（登录结果描述信息可从属性文件中获取，实现国际化）
 */
public enum LoginAuthResultEnums {
	LOGIN_SUCCESS(100, "登录成功"), // 登录成功
	LOGIN_FAIL_USER_NOTEXSIST(-101, "登录账号不存在，请注册"),    // 用户不存在
	LOGIN_FAIL_PWD_INCORRECT(-102, "账号或密码不正确"),         // 密码不正确
	LOGIN_FAIL_USER_LOCK(-103, "账号被锁定，请联系管理员"),        // 账号被锁定
	LOGIN_FAIL_EXCEPTION(-104, "登录发生异常，请联系管理员"),       // 登录发生异常
	LOGIN_FAIL_NOT_UNIQUE(-105, "账号不唯一，请联系管理员"),        // 账号不唯一
	LOGIN_FAIL_UNVISIT_AUTH(-106, "用户无访问权限，请联系管理员"),   // 没有访问权限
	LOGIN_FAIL_PARAM_NULL(-107, "账号或密码为空"),               // 用户名 或密码参数为空
	LOGIN_FAIL_REPEAT_LOGIN(-108, "已登录，重复的登录请求");

	private int code;

	private String msg;

	private String exception;

	public String getException() {
		return exception;
	}

	public LoginAuthResultEnums setException(String exception) {
		this.exception = exception;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	LoginAuthResultEnums(int code, String msg) {
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
