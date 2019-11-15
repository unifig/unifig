package etl.dispatch.config.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum IsSuccessEnum {
	SUCCESS(1, "执行成功"), FAIL(0, "执行失败");

	private int code;
	private String desc;

	private static final Map<String, IsSuccessEnum> lookup = new LinkedHashMap<String, IsSuccessEnum>();
	static {
		for (IsSuccessEnum s : EnumSet.allOf(IsSuccessEnum.class)) {
			lookup.put(s.getDesc(), s);
		}
	}

	IsSuccessEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

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
}
