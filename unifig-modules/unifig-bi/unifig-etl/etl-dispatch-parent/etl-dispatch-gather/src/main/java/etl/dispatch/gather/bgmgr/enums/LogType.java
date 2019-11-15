package etl.dispatch.gather.bgmgr.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum LogType {

	LOGIN(1, "login"), LOGOUT(2, "logout");

	private Integer code;
	private String desc;

	public static final Map<String, LogType> lookup = new LinkedHashMap<String, LogType>();
	static {
		for (LogType s : EnumSet.allOf(LogType.class)) {
			lookup.put(s.getDesc(), s);
		}
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private LogType(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	private LogType() {

	}

}
