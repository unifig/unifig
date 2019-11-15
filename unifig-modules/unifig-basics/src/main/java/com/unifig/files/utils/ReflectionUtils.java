package com.unifig.files.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by v-szyanb1 on 2017/7/13.
 */
public class ReflectionUtils {
    //设置对象的属性
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = getDeclaredField(obj, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Could not find field[" +
                    fieldName + "] on target [" + obj + "]");
        }

        makeAccessiable(field);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            System.out.println("不可能抛出的异常");
        }

    }

    //判断field的修饰符是否是public,并据此改变field的访问权限
    public static void makeAccessiable(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    //获取field属性，属性有可能在父类中继承
    public static Field getDeclaredField(Object obj, String fieldName) {
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {

            }
        }
        return null;
    }
}
