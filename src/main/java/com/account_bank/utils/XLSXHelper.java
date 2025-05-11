package com.account_bank.utils;

import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;

public abstract class XLSXHelper {

    protected Cell getCell(Sheet sheet, int rowNumber, int cellIndex) {
        Row row = sheet.getRow(rowNumber);
        row = row == null ? sheet.createRow(rowNumber) : row;
        Cell cell = row.getCell(cellIndex);
        return cell == null ? row.createCell(cellIndex) : cell;
    }

    protected String getStringValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return String.valueOf(cell.getDateCellValue());
                }
                return new BigDecimal(cell.getNumericCellValue()).toPlainString();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    protected double getDoubleValue(Cell cell) {
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            if (cell.getCellStyle().getDataFormatString().contains("%")) {
                return cell.getNumericCellValue() * 100;
            }
            return cell.getNumericCellValue();
        }
        return 0d;
    }
}
