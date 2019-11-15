package etl.dispatch.gather.api.umeng.entity;

import java.io.Serializable;

/**
 * 
 * @ClassName: Authorize
 * @Description: 友盟令牌
 * @date: 2017年11月30日 上午10:11:24
 */
public class Authorize implements Serializable {
	private static final long serialVersionUID = -7906616540451448338L;

	private Integer code;
	private String success;
	private String auth_token;
	private String error;

	public Authorize() {

	}

	public Authorize(Integer code, String success, String auth_token, String error) {
		super();
		this.code = code;
		this.success = success;
		this.auth_token = auth_token;
		this.error = error;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getAuth_token() {
		return auth_token;
	}

	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
