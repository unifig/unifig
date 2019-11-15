package etl.dispatch.util.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title:ClassUtil
 * @Description: 单个对象的操作帮助类
 */
public class ObjectClassHelper {

    private static final Logger logger = LoggerFactory.getLogger(ObjectClassHelper.class);

    /**
     * 给一个实例的字段设置一个值
     * 
     * @param target 一个实例
     * @param fname 字段名称
     * @param ftype 字段类型 自身类.class.isAssignableFrom(自身类或子类.class)  返回true 
     * @param fvalue 字段值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setFieldValue(Object target, String fname, Class ftype, Object fvalue) {
        if (target == null || fname == null || "".equals(fname)
            || (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {
            return;
        }
        Class clazz = target.getClass();
        try {
            Method method = clazz.getDeclaredMethod(
                "set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), ftype);
            if ( !Modifier.isPublic(method.getModifiers())) {
                method.setAccessible(true);
            }
            method.invoke(target, fvalue);

        } catch (Exception me) {
            try {
                Field field = clazz.getDeclaredField(fname);
                if ( !Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                field.set(target, fvalue);
            } catch (Exception fe) {
                logger.debug(fe.getMessage());
            }
        }
    }

    /**
     * 通过BeanUtils实现的设置一个实例的属性
     * 
     * @param target实例
     * @param fname 属性名
     * @param fvalue 属性值
     */
    public static void setFieldValue(Object target, String fname, Object fvalue) {
        try {
            BeanUtils.setProperty(target, fname, fvalue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到一个实例中的一个字段的值
     * 
     * @param target 实例
     * @param fname 字段名
     * @return 得到的字段值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object getFieldValue(Object target, String fname) {
        if (target == null || fname == null || fname.trim().length() <= 0)
            return null;
        if (target instanceof Map) {
            return ((Map)target).get(fname);
        }else{
            Class clazz = target.getClass();
            try {
                Method method = clazz.getDeclaredMethod(
                    "get" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), Object.class);
                if ( !Modifier.isPublic(method.getModifiers())) {
                    method.setAccessible(true);
                }
                return method.invoke(target, Object.class);
            } catch (Exception e) {
                try {
                    Field field = clazz.getDeclaredField(fname);
                    if ( !Modifier.isPublic(field.getModifiers())) {
                        field.setAccessible(true);
                    }
                    return field.get(target);
                } catch (Exception se) {
                    logger.debug(se.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 得到一个实例中的一个属性值，通过BeanUtils实现
     * 
     * @param target 实例
     * @param fname 属性名
     * @return 属性值
     */
    public static Object getPropertyValue(Object target, String fname) {
        return getPropertyValue(target, fname, true);
    }

    /**
     * 
     * 〈一句话功能简述〉
     * 〈功能详细描述〉
     * @param target 对象 javabean
     * @param fname  对象属性名称
     * @param caseSensitive 区分大小写
     * @return Object
     */
    @SuppressWarnings({"rawtypes"})
    public static Object getPropertyValue(Object target, String fname, boolean caseSensitive) {
        try {
            if (caseSensitive) {
                return BeanUtils.getProperty(target, fname);
            } else {
                if (target instanceof Map) {
                    Set keySet = ((Map)target).keySet();
                    Iterator it = keySet.iterator();
                    while (it.hasNext()) {
                        String mapKey = it.next() + "";
                        if (mapKey.equalsIgnoreCase(fname)) {
                            return BeanUtils.getProperty(target, mapKey);
                        }
                    }
                } else {
                    Class clazz = target.getClass();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        if (fieldName.equalsIgnoreCase(fname)) {
                            return BeanUtils.getProperty(target, fieldName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将一个实例转换成map，只转一层
     * 
     * @param target 实例
     * @return 转换后的map
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes"})
    public static Map convertToMap(Object target) {
        try {
            return BeanUtils.describe(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  对象copy复制赋值
     */
    @SuppressWarnings({"rawtypes"})
    public static void copyProperties(Object target, Object source, boolean caseSensitive) {
        if (target != null && source != null) {
            try {
                Class clazz = target.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    String key = field.getName();
                    if (caseSensitive) {
                        Object fieldValue = getPropertyValue(source, key);
                        if (fieldValue.getClass().equals(field.getType())) {
                            setFieldValue(target, key, fieldValue);
                        }
                    } else {
                        if (source instanceof Map) {
                            Set keySet = ((Map)source).keySet();
                            Iterator it = keySet.iterator();
                            while (it.hasNext()) {
                                String mapKey = it.next() + "";
                                if (mapKey.equalsIgnoreCase(key)) {
                                    Object fieldValue = getPropertyValue(source, mapKey);
                                    if (fieldValue.getClass().equals(field.getType())) {
                                        setFieldValue(target, key, fieldValue);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Class sourceClazz = source.getClass();
                            Field[] sourceFields = sourceClazz.getDeclaredFields();
                            for (Field f : sourceFields) {
                                if (f.getName().equalsIgnoreCase(key)) {
                                    Object fieldValue = getPropertyValue(source, f.getName());
                                    if (fieldValue.getClass().equals(field.getType())) {
                                        setFieldValue(target, key, fieldValue);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}