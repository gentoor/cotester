package org.cn.gentoor.common.cotester;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.cn.gentoor.common.cotester.util.ConvertUtil;
import org.cn.gentoor.common.cotester.util.ExcelDataUtil;
import org.cn.gentoor.common.cotester.util.HeaderAnalyzer;
import org.cn.gentoor.common.cotester.util.IExcelDataUtil;
import org.cn.gentoor.common.cotester.util.ParaObject;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gentoor on 2015/11/26.
 */
public class ExcelDataProvider {
    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getTestDataFromExcel(Method method)
            throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        return getTestDataFromExcelFile(method.getDeclaringClass(), method);
    }

    private static Object[][] getTestDataFromExcelFile(Class clazz, Method method)
            throws IOException, InvalidFormatException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        String clazzName = clazz.getName();
        String filePath = clazzName.replace('.', '/') + ".xls";
        String sheetName = method.getName();
        InputStream is = clazz.getClassLoader().getResourceAsStream(filePath);
        if(null == is) {
            // 如果没有打开.xls后缀的文件，就再次尝试.xlsx格式的文件
            filePath += 'x';
            is = clazz.getClassLoader().getResourceAsStream(filePath);
        }

        return getTestDataFromExcelSheet(clazz, method, is, sheetName);
    }

    private static Object[][] getTestDataFromExcelSheet(Class clazz, Method method, InputStream is, String sheetName)
            throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Type[] paraTypes = method.getGenericParameterTypes();
        int paraCnt = method.getParameterCount();

        IExcelDataUtil excelDataUtil = new ExcelDataUtil(is);
        String[] header = excelDataUtil.getHeaderData(sheetName);

        List<ParaObject> paraInfos = HeaderAnalyzer.analysisHeader(header, paraTypes, excelDataUtil);
        String[][] data = excelDataUtil.getBodyData(sheetName);
        Object[][] result = new Object[data.length][paraInfos.size()];

        for(int i=0; i<data.length; i++) {
            for(int j=0; j<paraInfos.size(); j++) {
                result[i][j]= ConvertUtil.convert(paraInfos.get(j), data[i], excelDataUtil);
            }
        }

        return result;
    }
}
