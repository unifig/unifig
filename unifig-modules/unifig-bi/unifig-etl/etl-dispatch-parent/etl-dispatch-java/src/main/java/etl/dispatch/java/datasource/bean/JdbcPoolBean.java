package etl.dispatch.java.datasource.bean;

import java.io.Serializable;

public class JdbcPoolBean implements Serializable {
	private static final long serialVersionUID = -4924297635224593071L;
	// 连接Url路径
	private String url;
	// 用户名
	private String user;
	// 密码
	private String password;
	// 驱动类路径
	private String driverClass;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

}
