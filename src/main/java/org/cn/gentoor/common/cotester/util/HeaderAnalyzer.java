package org.cn.gentoor.common.cotester.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gentoor on 2015/11/24.
 */
public class HeaderAnalyzer {
    public static List<ParaObject> analysisHeader(String[] header, Type[] paraTypes, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        List<ParaObject> paras = new ArrayList<ParaObject>(header.length);

        if(null == header || header.length == 0 || null == paraTypes || paraTypes.length == 0)
            return paras;

        for(int hi=0, pi=0;hi<header.length && pi<paraTypes.length;hi++) {
            pi += analysisColumn(paras, header[hi], paraTypes[pi], hi, excelDataUtil);
        }
        return paras;
    }

    private static void addArrayParaDefineToList(List<ParaObject> paras, String colName, Type paraType, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        // TODO....
    }
    private static void addCollectParaDefineToList(List<ParaObject> paras, String colName, Type paraType, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        ParaObject paraObject = addSimpleParaDefineToList(paras, colName, paraType, colNum);
        paraObject.setValueClass(TypeUtil.getValueClass(paraType));
        String refSheetName = "$" + colName;
        String[] header = excelDataUtil.getHeaderData(refSheetName);
        analysisRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getValueClass());
    }
    private static ParaObject addSimpleParaDefineToList(List<ParaObject> paras, String colName, Type paraType, int colNum) {
        ParaObject paraObject = new ParaObject();
        paras.add(paraObject);
        paraObject.setParaName(colName);
        paraObject.setParaClass(TypeUtil.getTypeClass(paraType));
        paraObject.setBeginCol(colNum);
        paraObject.setEndCol(colNum);
        return paraObject;
    }

    private static int analysisColumn(List<ParaObject> paras, String colName, Type paraType, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        Class clazz = TypeUtil.getTypeClass(paraType);
        if(colName.contains(".") && !TypeUtil.isSimpleClass(clazz) && !TypeUtil.isCollection(clazz) && TypeUtil.isMap(clazz)) {
            return addCombinedObjectToList(paras, colName, paraType, colNum, excelDataUtil);
        } else {
            return addSingleObjectToList(paras, colName, paraType, colNum, excelDataUtil);
        }
    }

    private static void addMapParaDefineToList(List<ParaObject> paras, String colName, Type paraType, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        ParaObject paraObject = addSimpleParaDefineToList(paras, colName, paraType, colNum);
        paraObject.setValueClass(TypeUtil.getValueClass(paraType));
        paraObject.setKeyClass(TypeUtil.getKeyClass(paraType));
        String refSheetName = "$" + colName;
        String[] header = excelDataUtil.getHeaderData(refSheetName);
        if(TypeUtil.isSimpleClass(paraObject.getKeyClass())) {
            addSingleObjectToList(paraObject.getSubParaObjs(), "key", ((ParameterizedType) paraType).getActualTypeArguments()[0], 0, excelDataUtil);
        } else {
            analysisMapRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getKeyClass(), "key.");
        }
        if(TypeUtil.isSimpleClass(paraObject.getValueClass())) {
            addSingleObjectToList(paraObject.getSubParaObjs(), "value", ((ParameterizedType)paraType).getActualTypeArguments()[1], header.length-1, excelDataUtil);
        } else {
            analysisMapRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getValueClass(), "value.");
        }
    }

    private static int addSingleObjectToList(List<ParaObject> paras, String colName, Type type, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        ParaObject paraObject = new ParaObject();
        paras.add(paraObject);
        paraObject.setParaName(colName);
        paraObject.setParaClass(TypeUtil.getTypeClass(type));
        paraObject.setBeginCol(colNum);
        paraObject.setEndCol(colNum);
        if(TypeUtil.isCollection(paraObject.getParaClass())) {
            paraObject.setValueClass(TypeUtil.getValueClass(type));
            String refSheetName = "$" + colName;
            String[] header = excelDataUtil.getHeaderData(refSheetName);
            analysisRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getValueClass());
        } else if(TypeUtil.isMap(paraObject.getParaClass())) {
            paraObject.setValueClass(TypeUtil.getValueClass(type));
            paraObject.setKeyClass(TypeUtil.getKeyClass(type));
            String refSheetName = "$" + colName;
            String[] header = excelDataUtil.getHeaderData(refSheetName);
            if(TypeUtil.isSimpleClass(paraObject.getKeyClass())) {
                addSingleObjectToList(paraObject.getSubParaObjs(), "key", ((ParameterizedType) type).getActualTypeArguments()[0], 0, excelDataUtil);
            } else {
                analysisMapRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getKeyClass(), "key.");
            }
            if(TypeUtil.isSimpleClass(paraObject.getValueClass())) {
                addSingleObjectToList(paraObject.getSubParaObjs(), "value", ((ParameterizedType)type).getActualTypeArguments()[1], header.length-1, excelDataUtil);
            } else {
                analysisMapRefereceHeader(paraObject.getSubParaObjs(), header, excelDataUtil, paraObject.getValueClass(), "value.");
            }
        }
        return 1;
    }

    private static void analysisMapRefereceHeader(List<ParaObject> paras, String[] header, IExcelDataUtil excelDataUtil, Class clazz, String prefix) throws NoSuchFieldException {
        if(null == header || header.length ==0 || null == clazz) return;
        for(int i=0; i<header.length; i++) {
            String paraName = header[i];
            if(!paraName.startsWith(prefix))   continue;
            String fieldName = paraName.substring(prefix.length());
            Type fieldType = TypeUtil.getFieldType(clazz, fieldName);
            analysisColumn(paras, paraName, fieldType, i, excelDataUtil);
        }
    }
    private static void analysisRefereceHeader(List<ParaObject> paras, String[] header, IExcelDataUtil excelDataUtil, Class clazz) throws NoSuchFieldException {
        analysisMapRefereceHeader(paras, header, excelDataUtil, clazz, "");
    }
    private static int addCombinedObjectToList(List<ParaObject> paras, String colName, Type type, int colNum, IExcelDataUtil excelDataUtil) throws NoSuchFieldException {
        String objName = colName.substring(0, colName.indexOf('.'));
        String fieldName = colName.substring(colName.indexOf('.')+1);
        ParaObject para = findParaInList(paras, objName);
        if(para != null) {
            para.setEndCol(colNum);
            Type fieldType = TypeUtil.getFieldType(para.getParaClass(), fieldName);
            analysisColumn(para.getSubParaObjs(), fieldName, fieldType, colNum, excelDataUtil);
            return 0;
        } else {
            para = new ParaObject();
            paras.add(para);
            para.setParaName(objName);
            para.setParaClass(TypeUtil.getTypeClass(type));
            para.setBeginCol(colNum);
            para.setEndCol(colNum);
            Type fieldType = TypeUtil.getFieldType(para.getParaClass(), fieldName);
            analysisColumn(para.getSubParaObjs(), fieldName, fieldType, colNum, excelDataUtil);
            return 1;
        }
    }

    private static ParaObject findParaInList(List<ParaObject> paras, String paraName) {
        for(ParaObject para : paras) {
            if(paraName.equals(para.getParaName())) return para;
        }
        return null;
    }



}
