package com.tools.plugin.utils.helper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对Bean集合进行按字段排序
 * 
 */
public class BeanListSortHelper<E> {

    private static final Logger logger = LoggerFactory.getLogger(BeanListSortHelper.class);

    /**
     * List 元素的多个属性进行排序。例如 ListSorter.sort(list,true,"name", "age")， 则先按 name 属性排序，name 相同的元素按 age 属性排序。
     * 
     * @param list 包含要排序元素的 List
     * @param isDesc 是否降序排列
     * @param properties 要排序的属性。前面的值优先级高。
     */
    public static <V> void sort(List<V> list, final boolean isDesc, final String... properties) {
        Collections.sort(list, new Comparator<V>() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            public int compare(V o1, V o2) {
                if (o1 == null && o2 == null)
                    return 0;
                if (o1 == null)
                    if (isDesc) {
                        return 1;
                    } else {
                        return -1;
                    }
                if (o2 == null)
                    if (isDesc) {
                        return -1;
                    } else {
                        return 1;
                    }
                for (String property : properties) {
                    if (property == null || property.length() == 0) {
                        continue;
                    }
                    Comparator c = new BeanComparator(property);
                    int result = c.compare(o1, o2);
                    if (result != 0) {
                        if (isDesc) {
                            return -result;
                        }
                        return result;
                    }
                }
                return 0;
            }
        });
    }

    /**
     * 根据参数（排序列，是否逆序）返回一个比较器
     * 
     * @return
     */
    public static <V> Comparator<V> getComparator(String property, boolean isDesc, String defaultProperty) {
        if (property == null) {
            if (defaultProperty != null) {
                property = defaultProperty;
            } else {
                throw new RuntimeException("property used to sort can't be NULL.");
            }
        }
        String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);

        return getComparatorByName(methodName, isDesc);
    }

    /**
     * 利用反射调用Bean的getter方法，再比较值。 如果getter方法调用失败，排序方法将失效。 如果getter方法返回值为Null，则认为此Bean小于另一Bean。
     * 当两个返回值同时为null时，认为第一个Bean更小。 其余情况下由compareTo方法决定比较结果。
     * 
     * @param methodName
     * @param isDesc
     * @return
     */
    private static <V> Comparator<V> getComparatorByName(final String methodName, final boolean isDesc) {
        return new Comparator<V>() {

            @SuppressWarnings({"rawtypes", "unchecked"})
            public int compare(V pO1, V pO2) {
                Comparable value1 = 0;
                Comparable value2 = 0;
                try {
                    Method getterMethod = ((Class<? extends Object>)pO1.getClass()).getMethod(methodName,
                        (Class<?>[])null);
                    value1 = (Comparable)getterMethod.invoke(pO1, (Object[])null);
                    value2 = (Comparable)getterMethod.invoke(pO2, (Object[])null);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return 0;
                }
                boolean isOneSmallerThanTwo = false;

                if (value1 == null) {
                    isOneSmallerThanTwo = true;
                } else if (value2 == null) {
                    isOneSmallerThanTwo = false;
                } else {
                    isOneSmallerThanTwo = value1.compareTo(value2) < 0;
                }
                return isOneSmallerThanTwo ^ isDesc ? -1 : 1;
            }
        };
    }
}