package com.tools.plugin.utils.system;

import java.util.Properties;

public class OsUtils {

	/**
	 * 〈判断调用脚本模式、windows系统使用批处理脚本，其它系统采用Shell脚本〉 〈功能详细描述〉
	 * 
	 * @return
	 */
	public static boolean isShellModel() {
		Properties props = System.getProperties(); // 获得系统属性集
		String osName = props.getProperty("os.name"); // 操作系统名称
		if (osName.indexOf("Windows") >= 0)
			return false;
		return true;
	}
}
