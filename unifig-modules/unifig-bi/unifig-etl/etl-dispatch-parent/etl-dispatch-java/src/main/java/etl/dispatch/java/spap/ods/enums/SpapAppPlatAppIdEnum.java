package etl.dispatch.java.spap.ods.enums;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 坐标目前 平台类型和应用固定映射枚举类 PS: 坐标服务器端数据不规范 , 需要传平台类型+应用ID 2个字段，目前只一个字段，需要手动映射)
 * 
 * @author 、
 */
public enum SpapAppPlatAppIdEnum {
	web(21, 6), // web端所传参数
	macos(25, 5), // mac电脑所传参数（注：这里需要处理 如果接收数据为 mac 需转换成 macos。服务器也会做转换）
	windows(15, 4), // windows电脑所传参数
	androidTV(14, 3), // androidTV端所传参数
	android(4, 2), // android端所传参数
	ios(3, 1),// iphone 端所传参数
	unknow(-9, -9);// iphone 端所传参数

	private int appPlatId;
	private int appId;

	public static final Map<Integer, SpapAppPlatAppIdEnum> lookup = new LinkedHashMap<Integer, SpapAppPlatAppIdEnum>();
	static {
		for (SpapAppPlatAppIdEnum s : EnumSet.allOf(SpapAppPlatAppIdEnum.class)) {
			lookup.put(s.getAppPlatId(), s);
		}
	}

	SpapAppPlatAppIdEnum(int appPlatId, int appId) {
		this.appPlatId = appPlatId;
		this.appId = appId;
	}

	public int getAppPlatId() {
		return appPlatId;
	}

	public void setAppPlatId(int appPlatId) {
		this.appPlatId = appPlatId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

}
