package org.cn.gentoor.common.cotester.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zszhang on 2015/11/18.
 */
public class ExcelDataUtil implements IExcelDataUtil {
    private final Workbook workbook;

    public ExcelDataUtil(InputStream inputStream)
            throws IOException, InvalidFormatException {
        workbook = WorkbookFactory.create(inputStream);
    }

    /**
     * 获取指定excel表格的数据
     * @param sheetName
     * @return
     */
    public String[][] getAllData(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row;
        Cell cell;
        int firstRow = sheet.getFirstRowNum();
        int lastRow = sheet.getLastRowNum();
        row = sheet.getRow(firstRow);
        if(null == row) return null;

        int firstCol = row.getFirstCellNum();
        int lastCol = row.getLastCellNum();
        return this.getData(sheet, firstRow, lastRow, firstCol, lastCol);
    }

    /**
     * get header info from the first row
     * @param sheetName
     * @return
     */
    @Override
    public String[] getHeaderData(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = sheet.getRow(0);
        if(null == row) return null;
        int firstCol = row.getFirstCellNum();
        int lastCol = row.getLastCellNum();
        String[] data = new String[lastCol - firstCol];

        for(int i=0; firstCol < lastCol; i++, firstCol++) {
            Cell cell = row.getCell(firstCol);
            data[i] = getCellValue(cell);
        }

        return data;
    }

    @Override
    public String[][] getBodyData(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row;
        Cell cell;
        int firstRow = 1;
        int lastRow = sheet.getLastRowNum();
        row = sheet.getRow(0);
        if(null == row) return null;

        int firstCol = row.getFirstCellNum();
        int lastCol = row.getLastCellNum();
        return this.getData(sheet, firstRow, lastRow, firstCol, lastCol);
    }

    private String[][] getData(Sheet sheet,  int beginRow, int lastRow, int firstCol, int lastCol) {
        if(beginRow>lastRow || null == sheet || firstCol > lastCol)    return null;
        lastRow = sheet.getLastRowNum() >= lastRow ? lastRow : sheet.getLastRowNum();
        String[][] data = new String[lastRow-beginRow+1][lastCol-firstCol];
        Row row = null;
        Cell cell = null;
        for(int i=0; beginRow <= lastRow; i++, beginRow++) {
            row = sheet.getRow(beginRow);
            if(null == row) continue;
            for(int j=0;j+firstCol<lastCol; j++) {
                cell = row.getCell(j);
                data[i][j] = getCellValue(cell);
            }
        }

        return data;
    }
    /**
     * 获取指定范围的行数据，行号从1开始
     *
     * @param sheetName
     * @param beginRow  1-based
     * @param lastRow   1-based
     * @return
     */
    @Override
    public String[][] getData(String sheetName, int beginRow, int lastRow, int firstCol, int lastCol) {
        Sheet sheet = workbook.getSheet(sheetName);
        return getData(sheet, beginRow-1, lastRow-1, firstCol, lastCol);
    }

    private static String getCellValue(Cell cell) {
        if(null == cell)    return null;
        Object v = null;
        int type = cell.getCellType() == Cell.CELL_TYPE_FORMULA ?  cell.getCachedFormulaResultType() : cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    v = cell.getDateCellValue();
                } else {
                    v = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                v = cell.getBooleanCellValue();
                break;
            default:
                // do nothing.
                //logger.debug("cell " + cell.getColumnIndex() + "convert fail...");
        }
        return v.toString();
    }

}
