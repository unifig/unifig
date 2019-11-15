package etl.dispatch.util;

import java.util.HashMap;
import java.util.Map;

public class NewMapUtil {
	private Map<String, Object> collect = new HashMap<>();

	public NewMapUtil() {
	}
	
	public NewMapUtil(String key, Object value) {
		collect.put(key, value);
	}

	public NewMapUtil set(String key, Object value) {
		collect.put(key, value);
		return this;
	}

	public NewMapUtil putAll(Map<String, Object> map) {
		if(null!=map && !map.isEmpty()){
			collect.putAll(map);
		}
		return this;
	}
	
	public Map<String, Object> get() {
		return collect;
	}
}
