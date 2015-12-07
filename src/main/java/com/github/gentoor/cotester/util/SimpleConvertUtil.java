package com.github.gentoor.cotester.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.DoubleSummaryStatistics;

/**
 * Created by gentoor on 2015/11/18.
 */
public class SimpleConvertUtil {
    @SuppressWarnings("unchecked")
    public static Object convert(Class type, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if( null == value || null == type) return null;

        if(type.isAssignableFrom(value.getClass())) return value;

        if(type.isPrimitive()) {
            // 基本数据类型，返回其封装类型
            return getPrimitiveValue(type, value);
        }

        return toSimpleObject(type, value);
    }

    private static Object getPrimitiveValue(Class type, Object value) {
        //  boolean, byte, char, short, int, long, float, and double.
        Object result = null;

        if("boolean".equals(type.getSimpleName()))
            result = toBoolean(value);
        else if("byte".equals(type.getSimpleName()))
            result = toByte(value);
        else if("char".equals(type.getSimpleName()))
            result = ((String)value).charAt(0);
        else if("short".equals(type.getSimpleName()))
            result = toShort(value);
        else if("int".equals(type.getSimpleName()))
            result = toInteger(value);
        else if("long".equals(type.getSimpleName()))
            result = toLong(value);
        else if("float".equals(type.getSimpleName()))
            result = toFloat(value);
        else if("double".equals(type.getSimpleName()))
            result = toDouble(value);

        return result;
    }

    private static Object toSimpleObject(Class type, Object value) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if(type.isAssignableFrom(Long.class))   return toLong(value);
        if(type.isAssignableFrom(Integer.class))    return toInteger(value);
        if(type.isAssignableFrom(Short.class))      return toShort(value);
        if(type.isAssignableFrom(Byte.class))       return toByte(value);
        if(type.isAssignableFrom(Character.class))  return ((String)value).charAt(0);
        if(type.isAssignableFrom(Boolean.class))    return toBoolean(value);
        if(type.isAssignableFrom(Double.class))     return toDouble(value);
        if(type.isAssignableFrom(Float.class))      return toFloat(value);

        String strValue = value.toString();
        Constructor constructor = type.getConstructor(String.class);
        return constructor.newInstance(strValue);
    }
    private static Boolean toBoolean(Object value) {
        if(value instanceof Boolean)    return (Boolean)value;
        if(value instanceof String)     return Boolean.valueOf((String)value);
        if(value instanceof Double) {
            return ((Double) value).intValue() == 0 ? false : true;
        }
        return false;
    }

    private static Integer toInteger(Object value) {
        if(value instanceof Double) return ((Double) value).intValue();
        if(value instanceof String) return Integer.valueOf((String)value);
        return null;
    }
    private static Byte toByte(Object value) {
        if(value instanceof Double) return ((Double) value).byteValue();
        if(value instanceof String) return Byte.valueOf((String)value);
        return null;
    }
    private static Short toShort(Object value) {
        if(value instanceof Double) return ((Double) value).shortValue();
        if(value instanceof String) return Short.valueOf((String)value);
        return null;
    }
    private static Long toLong(Object value) {
        if(value instanceof Double) return ((Double) value).longValue();
        if(value instanceof String) return Long.valueOf((String)value);
        return null;
    }
    private static Double toDouble(Object value) {
        if(value instanceof Double) return (Double) value;
        if(value instanceof String) return Double.valueOf((String)value);
        return null;
    }
    private static Float toFloat(Object value) {
        if(value instanceof Double) return ((Double) value).floatValue();
        if(value instanceof String) return Float.valueOf((String)value);
        return null;
    }

}
