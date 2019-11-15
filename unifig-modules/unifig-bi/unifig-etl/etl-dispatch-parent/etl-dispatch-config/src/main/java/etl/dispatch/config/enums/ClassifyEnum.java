package etl.dispatch.config.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum ClassifyEnum {
	TASK(1, "任务Success标记"), GROUP(2, "任务组Success标记");

	private int code;
	private String desc;

	private static final Map<String, ClassifyEnum> lookup = new LinkedHashMap<String, ClassifyEnum>();
	static {
		for (ClassifyEnum s : EnumSet.allOf(ClassifyEnum.class)) {
			lookup.put(s.getDesc(), s);
		}
	}

	ClassifyEnum(int code, String desc) {
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
