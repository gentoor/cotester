package com.github.gentoor.cotester.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by gentoor on 2015/11/18.
 */
public class SimpleConvertUtil {
    @SuppressWarnings("unchecked")
    public static Object convert(Class type, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if( null == value || null == type) return null;

        if(type.isPrimitive()) {
            // 基本数据类型，返回其封装类型
            return getPrimitiveValue(type, value);
        }

        Constructor constructor = type.getConstructor(String.class);
        return constructor.newInstance(value);
    }

    private static Object getPrimitiveValue(Class type, String value) {
        //  boolean, byte, char, short, int, long, float, and double.
        Object result = null;

        if("boolean".equals(type.getSimpleName()))
            result = Boolean.valueOf(value);
        else if("byte".equals(type.getSimpleName()))
            result = new Byte(value);
        else if("char".equals(type.getSimpleName()))
            result = value.charAt(0);
        else if("short".equals(type.getSimpleName()))
            result = new Short(value);
        else if("int".equals(type.getSimpleName()))
            result = new Integer(value);
        else if("long".equals(type.getSimpleName()))
            result = new Long(value);
        else if("float".equals(type.getSimpleName()))
            result = new Float(value);
        else if("double".equals(type.getSimpleName()))
            result = new Double(value);

        return result;
    }
}
