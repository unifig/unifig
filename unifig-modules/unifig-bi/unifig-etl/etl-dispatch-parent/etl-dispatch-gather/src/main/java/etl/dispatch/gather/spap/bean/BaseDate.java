package etl.dispatch.gather.spap.bean;

import java.io.Serializable;

public class BaseDate implements Serializable {

	private int code;
	private String desc;
	private ClassData data;

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

	public ClassData getData() {
		return data;
	}

	public void setData(ClassData data) {
		this.data = data;
	}
}
