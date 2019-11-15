package etl.dispatch.config.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum ScriptTypeEnum {
	JAVA(1, "Java脚本"), PYTHON(2, "Python脚本"), SHELL(3, "Shell脚本");

	private int code;
	private String desc;

	private static final Map<String, ScriptTypeEnum> lookup = new LinkedHashMap<String, ScriptTypeEnum>();
	static {
		for (ScriptTypeEnum s : EnumSet.allOf(ScriptTypeEnum.class)) {
			lookup.put(s.getDesc(), s);
		}
	}

	ScriptTypeEnum(int code, String desc) {
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
