package etl.dispatch.util.helper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * @Title:ObjectListHelper
 * @Description: 对list集合的操作帮助类，例如：对集合按照某属性进行分组，按某属性的值，从集合中挑拣出符合该值得数据，按照集合中的某个属性进行排序
 */
public class ObjectListHelper {
    /**
     * @Title: sortObjectByProperty
     * @Description: 按照集合中的属性，对List集合进行分组返回
     * @param it
     * @param propertyName
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Map<String, ArrayList> groupObjectByProperty(Iterator<T> it, String propertyName) {
        TreeMap tm = new TreeMap();
        if (it != null) {
            while (it.hasNext()) {
                T io = it.next();
                Object pvalue = ObjectClassHelper.getFieldValue(io, propertyName);
                if (tm.containsKey(pvalue)) {//
                    ArrayList List = (ArrayList)tm.get(pvalue);
                    List.add(io);
                } else {
                    ArrayList tem = new ArrayList();
                    tem.add(io);
                    tm.put(pvalue, tem);
                }
            }
        }
        return tm;
    }

    /**
     * @Title: findObjectByProperty
     * @Description: 〈在对象集合中，查找对象属性名、属性值等于参数值的对象子集〉
     * @param it
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public static <T> List<T> findObjectByProperty(Iterator<T> it, String propertyName, Object propertyValue) {
        List<T> result = null;
        if (it != null) {
            while (it.hasNext()) {
                T io = it.next();
                Object pvalue = ObjectClassHelper.getFieldValue(io, propertyName);
                if (pvalue != null) {
                    if ( (pvalue instanceof String) && propertyValue instanceof String) {
                        if ( ((String)pvalue).equalsIgnoreCase((String)propertyValue)) {
                            if (result == null)
                                result = new ArrayList<T>();
                            result.add(io);
                        }
                    } else {
                        if (pvalue.equals(propertyValue)) {
                            if (result == null)
                                result = new ArrayList<T>();
                            result.add(io);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @Title: findObjectByProperties
     * @Description: 〈在对象集合中，查找对象属性名数组、属性值数组等于参数值的对象子集〉，多个对象的属性值都满足才返回
     * @param it
     * @param propertiesName
     * @param propertiesValue
     * @return
     */
    public static <T> List<T> findObjectByProperties(Iterator<T> it, String[] propertiesName, Object[] propertiesValue) {
        List<T> result = null;
        if (it != null && propertiesName != null && propertiesValue != null) {
            while (it.hasNext()) {
                T io = it.next();
                boolean allTrue = true;
                for (int i = 0; i < propertiesName.length; i++ ) {
                    Object obj = ObjectClassHelper.getFieldValue(io, propertiesName[i]);
                    if (obj == null) {
                        allTrue = false;
                        break;
                    } else {
                        if ( (obj instanceof String) && (propertiesValue[i] instanceof String)) {
                            if ( ! ((String)obj).equalsIgnoreCase((String)propertiesValue[i])) {
                                allTrue = false;
                                break;
                            }
                        } else {
                            if ( !obj.equals(propertiesValue[i])) {
                                allTrue = false;
                                break;
                            }
                        }
                    }
                }
                if (allTrue) {
                    if (result == null)
                        result = new ArrayList<T>();
                    result.add(io);
                }
            }
        }
        return result;
    }
}