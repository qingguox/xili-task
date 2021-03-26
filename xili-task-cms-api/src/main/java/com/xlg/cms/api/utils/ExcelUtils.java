package com.xlg.cms.api.utils;

import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 */
public class ExcelUtils {

    // 标题行的字体大小,14
    private static final short HEADER_FONT_WEIGHT = (short) 14;

    public static Workbook createWorkBook() {
        return new XSSFWorkbook();
    }

    public static void writeHeader(Sheet sheet, List<String> strings) {
        writeHeaderOneSelf(sheet, strings, header(sheet.getWorkbook()));
    }

    /**
     * 功能描述: 创建标题（指定样式）
     */
    public static void writeHeaderOneSelf(Sheet sheet, List<String> values, CellStyle cellStyle) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < values.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values.get(i));
            cell.setCellStyle(cellStyle);
        }
    }

    private static CellStyle header(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 水平居中、垂直居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 白色边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.WHITE.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.WHITE.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.WHITE.getIndex());
        // 白字、字体14
        Font font = workbook.createFont();
        font.setFontHeightInPoints(HEADER_FONT_WEIGHT);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        // 蓝底
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        cellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    /**
     * 功能描述: 写单行数据
     */
    public static void writeRow(Sheet sheet, List<Object> values) {
        int lastRowNum = sheet.getLastRowNum();
        ++lastRowNum;

        // Excel2003版最大行数是65536行。Excel2007开始的版本最大行数是1048576行。

        Row row = sheet.createRow(lastRowNum);
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            Cell cell = row.createCell(i);
            if (null == value) {
                cell.setCellValue("");
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Boolean) {
                cell.setCellValue((boolean) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

}