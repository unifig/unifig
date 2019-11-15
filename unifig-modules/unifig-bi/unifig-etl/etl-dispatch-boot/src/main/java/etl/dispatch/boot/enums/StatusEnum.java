package etl.dispatch.boot.enums;

/**
 * 状态 1 - 正常 0 - 禁用 -1 - 已删除
 * 
 *
 *
 */
public enum StatusEnum {
	ENABLE(1, "正常"), DISABLE(0, "禁用"), DELETED(-1, "已删除");
	public int code;
	public String value;

	StatusEnum(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
