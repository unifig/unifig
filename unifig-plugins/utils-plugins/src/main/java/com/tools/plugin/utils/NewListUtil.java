package com.tools.plugin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * list对象创建工具类
 * 
 *
 *
 */
public class NewListUtil<V> {
	private List<V> collect = new ArrayList<>();

	public NewListUtil() {
	}

	public NewListUtil(V object) {
		collect.add(object);
	}

	public NewListUtil<V> add(V object) {
		collect.add(object);
		return this;
	}

	public NewListUtil<V> addAll(List<V> object) {
		if (null != object && !object.isEmpty()) {
			collect.addAll(object);
		}
		return this;
	}

	public List<V> get() {
		return collect;
	}
}
