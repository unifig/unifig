package etl.dispatch.gather.bgmgr.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public enum PlatType {
	
	web("web", "Browser".toLowerCase()), //web端所传参数
	android("android", "android".toLowerCase()), //android端所传参数
	ios("ios", "ios".toLowerCase()), // iphone 端所传参数
	macos("macos", "macos".toLowerCase()), //mac电脑所传参数（注：这里需要处理 如果接收数据为 mac 需转换成 macos。服务器也会做转换）
	windows("windows", "windows".toLowerCase()), //windows电脑所传参数
	Unknown("未知","未知".toLowerCase());

	private String desc;
	private String descLowerCase;

	public static final Map<String, PlatType> lookup = new LinkedHashMap<String, PlatType>();
	static {
		for (PlatType s : EnumSet.allOf(PlatType.class)) {
			lookup.put(s.getDescLowerCase(), s);
		}
	}

	PlatType(String desc, String descLowerCase) {
		this.desc = desc;
		this.descLowerCase = descLowerCase;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDescLowerCase() {
		return descLowerCase;
	}

	public void setDescLowerCase(String descLowerCase) {
		this.descLowerCase = descLowerCase;
	}

}
