package org.cn.gentoor.common.cotester.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gentoor on 2015/11/26.
 */
public class ConvertUtil {
    private static final Pattern pattern = Pattern.compile("^(?<firstRow>\\d+)?(?:-)?(?<lastRow>\\d+)?$");
    public static Object convert(ParaObject paraObject, String[] rowData, IExcelDataUtil excelDataUtil) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Object result = null;
        Class type = paraObject.getParaClass();

        if(TypeUtil.isCollection(type)) {
            result = cvt2Collect(rowData, paraObject, excelDataUtil);
        } else if(TypeUtil.isMap(type)) {
            result = cvt2Map(rowData, paraObject, excelDataUtil);
        } else if(TypeUtil.isSimpleClass(type)) {
            result = SimpleConvertUtil.convert(type, rowData[paraObject.getBeginCol()]);
        } else { // deal with combined object
            result = convert2CombinedObject(type, rowData, paraObject, excelDataUtil);
        }

        return result;
    }

    private static Object cvt2Map(String[] rowData, ParaObject paraInfo, IExcelDataUtil excelDataUtil) throws IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        String refStr = rowData[paraInfo.getBeginCol()];
        if(null == refStr) return null;
        Class mapClazz = paraInfo.getParaClass();
        Map result = (Map)TypeUtil.createRepresentativeInstance(mapClazz);

        RefereceInfo refereceInfo = getRefInfo(refStr, paraInfo.getParaName());
        String[][] refData = readRefData(refereceInfo, excelDataUtil, paraInfo);

        List<Object> keys = cvt2MapKeys(paraInfo, refData, excelDataUtil);
        List<Object> values = cvt2MapValues(paraInfo, refData, excelDataUtil);

        for(int i=0; i<keys.size(); i++) {
            result.put(keys.get(i), values.get(i));
        }
        return result;
    }

    private static List<Object> cvt2MapValues(ParaObject paraInfo, String[][] refDatra, IExcelDataUtil excelDataUtil) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        List<Object> values = new ArrayList<>();
        Class valueClass = paraInfo.getValueClass();

        if(TypeUtil.isSimpleClass(valueClass)) {
            cvt2SimpleCollection(valueClass, refDatra, values, true);
        } else {
            cvt2CombinedCollection(valueClass, paraInfo.getSubParaObjs(), "value.", refDatra, values, excelDataUtil);
        }

        return values;
    }
    private static List<Object> cvt2MapKeys(ParaObject paraInfo, String[][] refData, IExcelDataUtil excelDataUtil) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        List<Object> keys = new ArrayList<>();
        Class keyClass = paraInfo.getKeyClass();
        if(TypeUtil.isSimpleClass(keyClass)) {
            cvt2SimpleCollection(keyClass, refData, keys, false);
        } else {
            cvt2CombinedCollection(keyClass, paraInfo.getSubParaObjs(), "key.", refData, keys, excelDataUtil);
        }

        return keys;
    }
    private static Object cvt2Collect(String[] rowData, ParaObject paraInfo, IExcelDataUtil excelDataUtil) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        String refStr = rowData[paraInfo.getBeginCol()];
        if(null == refStr) return null;
        Class clazz = paraInfo.getParaClass();
        Collection result = (Collection)TypeUtil.createRepresentativeInstance(clazz);

        RefereceInfo refInfo = getRefInfo(refStr, paraInfo.getParaName());
        String[][] refData = readRefData(refInfo, excelDataUtil, paraInfo);

        if(TypeUtil.isSimpleClass(paraInfo.getValueClass())) {
            cvt2SimpleCollection(paraInfo.getValueClass(), refData, result, false);
        } else {
            cvt2CombinedCollection(paraInfo.getValueClass(), paraInfo.getSubParaObjs(), "", refData, result, excelDataUtil);
        }

        return result;
    }

    private static void cvt2SimpleCollection(Class valueClass, String[][] refData, Collection result, boolean isLastCol) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int col = isLastCol ? refData[0].length -1 : 0;
        for(int i=0; i<refData.length; i++) {
            result.add(SimpleConvertUtil.convert(valueClass, refData[i][col]));
        }
    }

    private static void cvt2CombinedCollection(Class clazz, List<ParaObject> fields, String fieldPrefix, String[][] refData, Collection result, IExcelDataUtil excelDataUtil) throws IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        for(int i=0; i<refData.length; i++) {
            Object o = clazz.newInstance();
            result.add(o);
            for(int j=0; j<fields.size(); j++) {
                String fieldName = fields.get(j).getParaName();
                if(!fieldName.startsWith(fieldPrefix)) continue;
                fieldName = fieldName.substring(fieldPrefix.length());
                Object fv = convert(fields.get(j), refData[i], excelDataUtil);
                Field field = TypeUtil.getField(clazz,fieldName);
                field.setAccessible(true);
                field.set(o, fv);
            }
        }
    }

    private static String[][] readRefData(RefereceInfo refInfo, IExcelDataUtil excelDataUtil, ParaObject para) {
        int firstRow = refInfo.getFirstRow() == null ? 2 : refInfo.getFirstRow();
        int lastRow = refInfo.getLastRow() == null ? Short.MAX_VALUE : refInfo.getLastRow();
        List<ParaObject> refParas = para.getSubParaObjs();
        if(refParas.size() == 0)  return null;
        int firstCol = refParas.get(0).getBeginCol();
        int lastCol = refParas.get(refParas.size()-1).getEndCol()+1;
        return excelDataUtil.getData(refInfo.getSheetName(), firstRow, lastRow, firstCol, lastCol);
    }
    private static RefereceInfo getRefInfo(String refStr, String paraName) {
        Matcher matcher = pattern.matcher(refStr);
        boolean found = matcher.find();
        if(!found) return null;
        RefereceInfo refInfo = new RefereceInfo();
        refInfo.setSheetName("$" + paraName);
        String tmp = matcher.group("firstRow");
        if( null != tmp && !tmp.isEmpty() )
            refInfo.setFirstRow(Integer.valueOf(tmp));
        tmp = matcher.group("lastRow");
        if( null != tmp && !tmp.isEmpty() )
            refInfo.setLastRow(Integer.valueOf(tmp));

        return refInfo;
    }

    private static Object convert2CombinedObject(Class clazz, String[] rowData, ParaObject paraInfo, IExcelDataUtil excelDataUtil)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        Object result = clazz.newInstance();
        for(ParaObject field : paraInfo.getSubParaObjs()) {
            Class type = field.getParaClass();
            if(TypeUtil.isSimpleClass(type)) {
                setFieldValue(result, clazz, field, rowData[field.getBeginCol()]);
            } else {
                setFieldValue(result, clazz, field, convert(paraInfo, rowData, excelDataUtil));
            }
        }
        return result;
    }

    private static void setFieldValue(Object result, Class clazz,  ParaObject field, Object value) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class fieldType = field.getParaClass();
        Object ov = value;
        if(value instanceof String)
            ov = SimpleConvertUtil.convert(fieldType, (String)value);
        Field fd = clazz.getDeclaredField(field.getParaName());
        fd.setAccessible(true);
        fd.set(result, ov);
    }
}
