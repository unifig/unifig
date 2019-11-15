package com.tools.plugin.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举工具类，对枚举类进行操作
 *
 *
 */
public class EnumUtil {
	public static Map<Integer, String> parseEnum(String clazz) throws Exception {
		if (clazz.startsWith("enum:")) {
			clazz = clazz.replace("enum:", "");
		} else {
			return null;
		}

		Class<?> ref = forName(clazz);

		if (null == ref) {
			throw new ClassNotFoundException(String.format("%s class not fond ", clazz));
		}

		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		if (ref.isEnum()) {
			Object[] ts = ref.getEnumConstants();
			for (Object t : ts) {
				Enum<?> e = (Enum<?>) t;
				map.put(e.ordinal(), e.name());
			}
		}
		return map;
	}

	static Class<?> forName(String className) throws Exception {
		String[] packageNames = className.split("\\.");
		String tempClassName = "";
		Class<?> returnClazz = null;
		for (String packageName : packageNames) {
			tempClassName += packageName;
			if (fristCharIsUpper(packageName)) {
				if (null == returnClazz) {
					try {
						returnClazz = Class.forName(tempClassName);
					} catch (Exception e) {
					}
				} else {
					Class<?> classes[] = returnClazz.getDeclaredClasses();
					for (Class<?> tempClazz : classes) {
						if (tempClazz.getSimpleName().equals(packageName)) {
							returnClazz = tempClazz;
						}
					}
				}
			}
			tempClassName += ".";
		}
		return returnClazz;
	}

	static boolean fristCharIsUpper(String name) {
		if (null != name && name.length() > 0) {
			char[] chars = name.toCharArray();
			int intChar = (int) chars[0];
			return intChar <= 90 && intChar >= 65;
		}
		return false;
	}
}