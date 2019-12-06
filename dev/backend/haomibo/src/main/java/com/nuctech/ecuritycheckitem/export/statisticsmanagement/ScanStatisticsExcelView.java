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
import org.apache.poi.ss.usermodel.*;
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
        headerCellNo.setCellValue("序号");

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue("时间段");

        Cell headerCellTotalScan = header.createCell(2);
        headerCellTotalScan.setCellValue("扫描总量");

        Cell headerCellValidScans = header.createCell(3);
        headerCellValidScans.setCellValue("有效扫描量");

        Cell headerCellValidScanRate = header.createCell(4);
        headerCellValidScanRate.setCellValue("有效率");

        Cell headerCellInvalidScans = header.createCell(5);
        headerCellInvalidScans.setCellValue("无效扫描量");

        Cell headerCellInvalidScanRate = header.createCell(6);
        headerCellInvalidScanRate.setCellValue("无效率");

        Cell headerCellPassedScans = header.createCell(7);
        headerCellPassedScans.setCellValue("通过量");

        Cell headerCellPassedScanRate = header.createCell(8);
        headerCellPassedScanRate.setCellValue("通过率");

        Cell headerCellAlarmScans = header.createCell(9);
        headerCellAlarmScans.setCellValue("报警量");

        Cell headerCellAlarmScanRate = header.createCell(10);
        headerCellAlarmScanRate.setCellValue("报警率");

    }


    public static InputStream buildExcelDocument(TreeMap<Long, ScanStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("ScanStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("扫描统计");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Long, ScanStatistics> entry : detailedStatistics.entrySet()) {

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
