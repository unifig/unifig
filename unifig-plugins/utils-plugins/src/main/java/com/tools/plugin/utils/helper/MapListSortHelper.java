package com.tools.plugin.utils.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
                        } else if (Integer.parseInt(m2) > Integer.parseInt(m1)) {
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
                        } else if (Integer.parseInt(m1) > Integer.parseInt(m2)) {
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
    
    /**
     * 对Map集合进行排序
     * 
     * @param list Map集合
     * @param propertyName 排序字段
     * @param sort 排序 desc、asc
     * @param isNumeral 是否为数字
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Sort2(List list, final String propertyName, final String sort, final boolean isNumeral) {
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
                        } else if (Double.parseDouble(m2) > Double.parseDouble(m1)) {
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
                        } else if (Integer.parseInt(m1) > Integer.parseInt(m2)) {
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
}