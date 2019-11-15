package com.tools.plugin.utils.helper;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MapSortHelper {
	public static void main(String[] args) {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("KFC", "kfc");
		map.put("WNBA", "wnba");
		map.put("NBA", "nba");
		map.put("CBA", "cba");
		Map<String, Object> resultMap = sortMapByKey(map); // 按Key进行排序
		for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}
}
// 比较器类
class MapKeyComparator implements Comparator<String> {
	public int compare(String str1, String str2) {
		return str1.compareTo(str2);
	}
}
