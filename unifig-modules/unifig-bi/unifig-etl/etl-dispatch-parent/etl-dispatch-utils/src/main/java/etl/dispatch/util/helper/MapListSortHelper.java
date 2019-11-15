package etl.dispatch.util.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 〈功能详细描述〉 按集合里某元素的某个属性进行排序
 */
public class MapListSortHelper {

    /**
     * 对Map集合进行排序
     * 
     * @param list Map集合
     * @param propertyName 排序字段
     * @param sort 排序 desc、asc
     * @param isNumeral 是否为数字
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Sort(List list, final String propertyName, final String sort, final boolean isNumeral) {
        Collections.sort(list, new Comparator() {

            public int compare(Object a, Object b) {
                int ret = 0;
                String m1 = String.valueOf( ((Map)a).get(propertyName));
                String m2 = String.valueOf( ((Map)b).get(propertyName));
                if (sort != null && "desc".equals(sort.toLowerCase())) {
                    // 倒序
                    if (isNumeral) {
                        if ("null".equals(m1) && "".equals(m1)) {
                            return 1;
                        } else if ("null".equals(m2) && "".equals(m2)) {
                            return -1;
                        } else if (Float.parseFloat(m2) > Float.parseFloat(m1)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        ret = m2.compareTo(m1);
                    }

                } else {
                    // 正序
                    if (isNumeral) {
                        if ("null".equals(m1) && "".equals(m1)) {
                            return -1;
                        } else if ("null".equals(m2) && "".equals(m2)) {
                            return 1;
                        } else if (Float.parseFloat(m1) > Float.parseFloat(m2)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        ret = m1.compareTo(m2);
                    }
                }
                return ret;
            }
        });
    }
    
    public static void main(String arges[]){
    	List<Map>  mapList= new ArrayList<>();
    	for(int i=10 ;i<24;i++){
    		Map testMap = new HashMap();
    		testMap.put("hour", i);
    		mapList.add(testMap);
    	}
    	
    	for(int i=0 ;i<10;i++){
    		Map testMap = new HashMap();
    		testMap.put("hour", i);
    		mapList.add(testMap);
    	}
    	MapListSortHelper sortHelper=  new MapListSortHelper();
    	sortHelper.Sort(mapList, "hour", "asc", true);
    	System.out.println(JSON.toJSONString(mapList));
    }
    
}