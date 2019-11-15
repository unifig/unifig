package etl.dispatch.gather.spap.bean;

import java.io.Serializable;

public class BaseRegister implements Serializable {

	private int code;
	private String desc;
	private RegisterList data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public RegisterList getData() {
		return data;
	}
	public void setData(RegisterList data) {
		this.data = data;
	}
	
}
