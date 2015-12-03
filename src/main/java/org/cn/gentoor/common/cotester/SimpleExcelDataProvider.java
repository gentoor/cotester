package org.cn.gentoor.common.cotester;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.cn.gentoor.common.cotester.util.ExcelDataUtil;
import org.cn.gentoor.common.cotester.util.IExcelDataUtil;
import org.cn.gentoor.common.cotester.util.SimpleConvertUtil;
import org.cn.gentoor.common.cotester.util.TypeUtil;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by zszhang on 2015/11/18.
 */
public class SimpleExcelDataProvider {
    @DataProvider(name="SimpleExcelDataProvider")
    public static Object[][] getTestDataFromExcel(Method method)
            throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return getTestDataFromExcelFile(method.getDeclaringClass(), method);
    }

    private static Object[][] getTestDataFromExcelFile(Class clazz, Method method)
            throws IOException, InvalidFormatException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String clazzName = clazz.getName();
        String filePath = clazzName.replace('.', '/') + ".xls";
        String sheetName = method.getName();
        InputStream is = clazz.getClassLoader().getResourceAsStream(filePath);
        if(null == is) {
            // 如果没有打开.xls后缀的文件，就再次尝试.xlsx格式的文件
            is = clazz.getClassLoader().getResourceAsStream(filePath+'x');
        }

        return getTestDataFromExcelSheet(clazz, method, is, sheetName);
    }

    private static Object[][] getTestDataFromExcelSheet(Class clazz, Method method, InputStream is, String sheetName)
            throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Type[] paraTypes = method.getGenericParameterTypes();
        int paraCnt = method.getParameterCount();

        IExcelDataUtil excelDataUtil = new ExcelDataUtil(is);
        String[][] data = excelDataUtil.getAllData(sheetName);
        int testCnt = data.length;
        Object[][] result = new Object[testCnt][paraCnt];

        for(int i=0; i<testCnt; i++) {
            for(int j=0; j<paraCnt && j<data[i].length; j++) {
                result[i][j] = SimpleConvertUtil.convert(TypeUtil.getTypeClass(paraTypes[j]), data[i][j]);
            }
        }

        return result;
    }
}
