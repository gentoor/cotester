package com.github.gentoor.cotester.util;

/**
 * Created by gentoor on 2015/11/21.
 */
public interface IExcelDataUtil {
    /**
     * 获取指定excel表格的数据
     * @param sheetName
     * @return
     */
    String[][] getAllData(String sheetName);

    String[] getHeaderData(String sheetName);

    String[][] getBodyData(String sheetName);

    /**
     * 获取指定范围的行数据，行号从1开始
     * @param sheetName
     * @param beginRow 1-based
     * @param lastRow 1-based
     * @return
     */
    String[][] getData(String sheetName, int beginRow, int lastRow, int firstCol, int lastCol);
}
