package com.company.kassa.utils;

import com.company.kassa.dto.product.ProductResponse;
import com.company.kassa.dto.product.ProductTransactionExcel;
import com.company.kassa.models.Kassa;
import com.company.kassa.models.MoneyTransaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelUtil {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);


    public static byte[] generateProductTransactionHistoryExcel(List<ProductTransactionExcel> fileList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ProductTransaction History");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "From User", "To User", "Transaction Date",
                    "Total Price", "Completed",
                    "Product Name", "Quantity", "Price"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (ProductTransactionExcel file : fileList) {
                //Transaction row
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                createCell(row, colNum++, file.getFromUserName());
                createCell(row, colNum++, file.getToUserName());
                createDateCell(
                        row,
                        colNum++,
                        file.getTransactionDate()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant(),
                        dateStyle
                );
                createDecimalCell(row, colNum++, file.getTotalPrice(), numberStyle);
                createCell(row, colNum++, file.getIsCompleted());

                colNum += 3;

                //  Product rows
                if (file.getProducts() != null && !file.getProducts().isEmpty()) {
                    for (ProductResponse product : file.getProducts()) {

                        Row productRow = sheet.createRow(rowNum++);
                        int productCol = 0;

                        productCol += 5;

                        createCell(productRow, productCol++, product.getName());
                        createDecimalCell(productRow, productCol++, BigDecimal.valueOf(product.getQuantity()), numberStyle);
                        createDecimalCell(productRow, productCol++, product.getPrice(), numberStyle);
                    }
                }

                // 🔹 spacing row
                rowNum++;
            }

            //Auto-size ALL columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateKassaExcel(List<Kassa> kassaList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Kassa History");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Owner", "Terminal",
                    "Card", "Cash",
                    "Total Amount", "Kassa Date", "isCompleted"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Kassa kassa : kassaList) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                createCell(row, colNum++, kassa.getOwner().getFullName());
                createDecimalCell(row, colNum++, kassa.getTerminal(), numberStyle);
                createDecimalCell(row, colNum++, kassa.getCard(), numberStyle);
                createDecimalCell(row, colNum++, kassa.getCash(), numberStyle);
                createDecimalCell(row, colNum++, kassa.getTotaAmount(), numberStyle);
                createDateCell(
                        row,
                        colNum++,
                        kassa.getKassaDate()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant(),
                        dateStyle
                );
                createCell(row, colNum++, kassa.getIsCompleted());
            }

            //Auto-size ALL columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateMoneyTransactionFile(List<MoneyTransaction> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Money Transaction History");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "From User", "To User",
                    "Amount", "TransactionDate",
                    "Money Type", "isCompleted"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (MoneyTransaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                createCell(row, colNum++, transaction.getFromUser().getFullName());
                createCell(row, colNum++, transaction.getToUser().getFullName());
                createDecimalCell(row, colNum++, transaction.getAmount(), numberStyle);
                createDateCell(
                        row,
                        colNum++,
                        transaction.getTransactionDate()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant(),
                        dateStyle
                );
                createCell(row, colNum++, transaction.getMoneyType());
                createCell(row, colNum++, transaction.getIsCompleted());

            }

            //Auto-size ALL columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private static void createCell(Row row, int column, Object value) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static void createDateCell(Row row, int column, Instant instant, CellStyle dateStyle) {
        Cell cell = row.createCell(column);
        if (instant != null) {
            cell.setCellValue(DATE_FORMATTER.format(instant));
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(dateStyle);
    }

    private static void createDecimalCell(Row row, int column, BigDecimal value, CellStyle numberStyle) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(numberStyle);
        } else {
            cell.setCellValue("");
        }
    }

    private static void createDoubleCell(Row row, int column, Double value, CellStyle numberStyle) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value);
            cell.setCellStyle(numberStyle);
        } else {
            cell.setCellValue("");
        }
    }


}
