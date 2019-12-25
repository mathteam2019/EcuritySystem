/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class ScanStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

        Cell headerCellTotalScan = header.createCell(2);
        headerCellTotalScan.setCellValue(messageSource.getMessage("TotalScan", null, currentLocale));

        Cell headerCellValidScans = header.createCell(3);
        headerCellValidScans.setCellValue(messageSource.getMessage("ValidScans", null, currentLocale));

        Cell headerCellValidScanRate = header.createCell(4);
        headerCellValidScanRate.setCellValue(messageSource.getMessage("ValidScanRate", null, currentLocale));

        Cell headerCellInvalidScans = header.createCell(5);
        headerCellInvalidScans.setCellValue(messageSource.getMessage("InvalidScans", null, currentLocale));

        Cell headerCellInvalidScanRate = header.createCell(6);
        headerCellInvalidScanRate.setCellValue(messageSource.getMessage("InvalidScanRate", null, currentLocale));

        Cell headerCellPassedScans = header.createCell(7);
        headerCellPassedScans.setCellValue(messageSource.getMessage("PassedScans", null, currentLocale));

        Cell headerCellPassedScanRate = header.createCell(8);
        headerCellPassedScanRate.setCellValue(messageSource.getMessage("PassedScanRate", null, currentLocale));

        Cell headerCellAlarmScans = header.createCell(9);
        headerCellAlarmScans.setCellValue(messageSource.getMessage("AlarmScans", null, currentLocale));

        Cell headerCellAlarmScanRate = header.createCell(10);
        headerCellAlarmScanRate.setCellValue(messageSource.getMessage("AlarmScanRate", null, currentLocale));

    }


    public static InputStream buildExcelDocument(TreeMap<Integer, ScanStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("ScanStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("ScanStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getTotalScan());
                row.createCell(3).setCellValue(record.getValidScan());
                row.createCell(4).setCellValue(df.format(record.getValidScanRate()));
                row.createCell(5).setCellValue(record.getInvalidScan());
                row.createCell(6).setCellValue(df.format(record.getInvalidScanRate()));
                row.createCell(7).setCellValue(record.getPassedScan());
                row.createCell(8).setCellValue(df.format(record.getPassedScanRate()));
                row.createCell(9).setCellValue(record.getAlarmScan());
                row.createCell(10).setCellValue(df.format(record.getAlarmScanRate()));


            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
