package com.tools.plugin.utils;

import java.util.List;
import java.util.Set;

/**
 * 字段串工具类
 *
 *
 */
public class StringUtil {
	
	public static boolean isNullOrEmpty(Object str) {
		return isNullOrEmpty(String.valueOf(str));
	}
	/**
	 * 判断字符串是否为空或为空值。<br/>
	 * 详细描述：判断字符串是否为空或为空值。<br/>
	 * 使用方式：通过本类的类名直接调用该方法，并传入所需参数。<br/>
	 * 
	 * @param str 字符串。<br/>
	 * @return flag true表示参数是为空或为空值，false则表示不为空或空值。<br/>
	 */
	public static boolean isNullOrEmpty(String str) {
		boolean flag = false;
		if (null == str || "".equals(str.trim()) || "n/a".equals(str.trim().toLowerCase()) || "[]".equals(str.trim().toLowerCase())  || "null".equals(str.trim().toLowerCase()) || "undefined".equals(str.trim().toLowerCase())) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 批量判断是否为空
	 * @param strs
	 * @return
	 */
	public static boolean isNullOrEmpty(String[] strs) {
		boolean flag = false;
		if (null != strs && strs.length > 0) {
			for (String str : strs) {
				if (null == str || "".equals(str.trim()) || "n/a".equals(str.trim().toLowerCase()) || "null".equals(str.trim().toLowerCase()) || "undefined".equals(str.trim().toLowerCase())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	
	/**
	 * 判断是否为空
	 * @param strs
	 * @return
	 */
	public static boolean isNullOrEmpty(byte[] strs) {
		boolean flag = true;
		if (null != strs && strs.length > 0) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 判断是否为空
	 * @param strs
	 * @return
	 */
	public static boolean isNullOrEmpty(Set<String> strs) {
		boolean flag = true;
		if (null != strs && !strs.isEmpty()) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 批量判断是否为空
	 * @param strs
	 * @return
	 */
	public static boolean isNullOrEmpty(List<String> strs) {
		boolean flag = false;
		if (null != strs && strs.size() > 0) {
			for (String str : strs) {
				if (null == str || "".equals(str.trim()) || "n/a".equals(str.trim().toLowerCase()) || "null".equals(str.trim().toLowerCase()) || "undefined".equals(str.trim().toLowerCase())) {
					flag = true;
				}
			}
		}
		return flag;
	}
}
