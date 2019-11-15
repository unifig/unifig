package etl.dispatch.gather.bgmgr.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @Description: 日程动作类型
 * @author: ylc
 */
public enum ScheduleActionType {

	ADD(1,"add"),DELEDT(2,"delete"),COMPLETE(3,"complete"),SHARE(4,"share");
	
	private Integer code;
	private String desc;

	public static final Map<String, ScheduleActionType> lookup = new LinkedHashMap<String, ScheduleActionType>();
	static {
		for (ScheduleActionType s : EnumSet.allOf(ScheduleActionType.class)) {
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
	
	private ScheduleActionType(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	private ScheduleActionType() {
	}
	
}
