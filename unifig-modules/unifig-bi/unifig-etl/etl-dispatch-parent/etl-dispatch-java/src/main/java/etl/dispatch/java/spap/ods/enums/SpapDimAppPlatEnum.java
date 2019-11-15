package etl.dispatch.java.spap.ods.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 平台类型中文枚举类
 * 
 * @author 、
 */
public enum SpapDimAppPlatEnum {
	pc("pc", "pc".toLowerCase()), //这是坐标2.0版本之前的 PC端所传，为了版本兼容所以保留了此项
	web("web", "web".toLowerCase()), //web端所传参数
	android("android", "android".toLowerCase()), //android端所传参数
	ios("ios", "ios".toLowerCase()), // iphone 端所传参数
	macos("macos", "macos".toLowerCase()), //mac电脑所传参数（注：这里需要处理 如果接收数据为 mac 需转换成 macos。服务器也会做转换）
//	mac("macos", "mac".toLowerCase()), //mac电脑所传参数（注：这里需要处理 如果接收数据为 mac 需转换成 macos。服务器也会做转换）
	windows("windows", "windows".toLowerCase()), //windows电脑所传参数
	tv("tv", "tv".toLowerCase()), //web端所传参数
	Unknown("未知","未知".toLowerCase());

	private String desc;
	private String descLowerCase;

	public static final Map<String, SpapDimAppPlatEnum> lookup = new LinkedHashMap<String, SpapDimAppPlatEnum>();
	static {
		for (SpapDimAppPlatEnum s : EnumSet.allOf(SpapDimAppPlatEnum.class)) {
			lookup.put(s.getDescLowerCase(), s);
		}
	}

	SpapDimAppPlatEnum(String desc, String descLowerCase) {
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
