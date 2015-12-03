package org.cn.gentoor.common.cotester.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by zszhang on 2015/11/27.
 */
public class TypeUtil {
    public static Type getFieldType(Class clazz, String fieldName) throws NoSuchFieldException {
        if(fieldName.contains(".")) {
            fieldName = fieldName.substring(0, fieldName.indexOf('.'));
        }
        Field field = getField(clazz, fieldName);
        Type type = field.getGenericType();
        return type;
    }

    public static Class getTypeClass(Type type) {
        try {
            if(type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                return Class.forName(ptype.getRawType().getTypeName());
            } else {
                return Class.forName(type.getTypeName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取指定类的指定字段名的Field, 如果找不到就返回空。
     * 查找范围包括父、子类的所有字段
     */
    public static  Field getField(final Class<?> clazz, final String fieldName) {
        if( null == clazz || null == fieldName || fieldName.isEmpty() ) return null;

        for(Class<?> c = clazz; c != Object.class ; c = c.getSuperclass() ) {
            Field[] sf = c.getDeclaredFields();
            for( Field e : sf ) {
                if( e.getName().equals(fieldName) )		return e;
            }
        }

        return null;
    }
    public static boolean isSimpleClass(Class clazz) {
        if(clazz.isPrimitive()) return true;

        if("java.lang.Boolean".equals(clazz.getName())) return true;

        if(Number.class.isAssignableFrom(clazz))    return true;

        if("java.util.Date".equals(clazz.getName()))    return true;

        if(clazz.getName().startsWith("java.lang.String"))  return true;

        return false;
    }
    public static Class getValueClass(Type type) {
        Type[] ptypes = null;
        try {
            ptypes = ((ParameterizedType) type).getActualTypeArguments();
            if(ptypes.length == 1) {
                return Class.forName(ptypes[0].getTypeName());
            } else {
                return Class.forName(ptypes[1].getTypeName());
            }
        } catch (ClassCastException e) {
            System.out.println("type is not a parameterized type, do nothing:" +  type);
        } catch (ClassNotFoundException e) {
            System.err.println("Class Not found!!!: " + ptypes);
        }
        return null;
    }

    public static Class getKeyClass(Type type) {
        Type[] ptypes = null;
        try {
            ptypes = ((ParameterizedType) type).getActualTypeArguments();
            if(ptypes.length == 2) {
                return Class.forName(ptypes[0].getTypeName());
            }
        } catch (ClassCastException e) {
            System.out.println("type is not a parameterized type, do nothing:" +  type);
        } catch (ClassNotFoundException e) {
            System.err.println("Class Not found!!!: " + ptypes);
        }
        return null;
    }

    public static boolean isCollection(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isMap(Class clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    private static Object createInstance(Class clazz) {
        Object o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static Object createRepresentativeInstance(Class clazz) throws IllegalAccessException, InstantiationException {
        if(!clazz.isInterface())    return clazz.newInstance();

        String clazzName = clazz.getName();
        if("java.util.Collection".equals(clazzName) || "java.util.List".equals(clazzName))  return new ArrayList<>();
        if(Set.class.isAssignableFrom(clazz))   return new TreeSet<>();
        if(Queue.class.isAssignableFrom(clazz)) return new ConcurrentLinkedDeque<>();
        if(Map.class.isAssignableFrom(clazz))   return new ConcurrentSkipListMap();

        return null;
    }
}
