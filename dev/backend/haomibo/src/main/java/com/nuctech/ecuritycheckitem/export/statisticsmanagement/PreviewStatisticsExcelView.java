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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;

public class PreviewStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

        Cell headerCellTotalScan = header.createCell(2);
        headerCellTotalScan.setCellValue(messageSource.getMessage("TotalScan", null, currentLocale));

        Cell headerCellInvalidScans = header.createCell(3);
        headerCellInvalidScans.setCellValue(messageSource.getMessage("InvalidScans", null, currentLocale));

        Cell headerInvalidScanRate = header.createCell(4);
        headerInvalidScanRate.setCellValue(messageSource.getMessage("InvalidScanRate", null, currentLocale));

        Cell headerCellTotalJudge = header.createCell(5);
        headerCellTotalJudge.setCellValue(messageSource.getMessage("TotalJudge", null, currentLocale));

        Cell headerCellTotalHands = header.createCell(6);
        headerCellTotalHands.setCellValue(messageSource.getMessage("TotalHands", null, currentLocale));

        Cell headerCellNosuspicion = header.createCell(7);
        headerCellNosuspicion.setCellValue(messageSource.getMessage("Nosuspicion", null, currentLocale));

        Cell headerCellScanNosuspictionRate = header.createCell(8);
        headerCellScanNosuspictionRate.setCellValue(messageSource.getMessage("ScanNosuspictionRate", null, currentLocale));

        Cell headerCellNoSeizure = header.createCell(9);
        headerCellNoSeizure.setCellValue(messageSource.getMessage("NoSeizure", null, currentLocale));

        Cell headerCellNoSeizureRate = header.createCell(10);
        headerCellNoSeizureRate.setCellValue(messageSource.getMessage("NoSeizureRate", null, currentLocale));

        Cell headerCellSeizure = header.createCell(11);
        headerCellSeizure.setCellValue(messageSource.getMessage("Seizure", null, currentLocale));

        Cell headerCellSeizureRate = header.createCell(12);
        headerCellSeizureRate.setCellValue(messageSource.getMessage("SeizureRate", null, currentLocale));

    }


    public static InputStream buildExcelDocument(TreeMap<Long, TotalStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Preview-Statistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("PreviewStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 0;
            int counter = 4;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {
                TotalStatistics record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getScanStatistics().getTotalScan());
                row.createCell(3).setCellValue(record.getScanStatistics().getInvalidScan());
                row.createCell(4).setCellValue(df.format(record.getScanStatistics().getInvalidScanRate()));
                row.createCell(5).setCellValue(record.getJudgeStatistics().getTotalJudge());
                row.createCell(6).setCellValue(record.getHandExaminationStatistics().getTotalHandExamination());
                row.createCell(7).setCellValue(record.getJudgeStatistics().getNoSuspictionJudge());
                row.createCell(8).setCellValue(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                row.createCell(9).setCellValue(record.getHandExaminationStatistics().getSeizureHandExamination());
                row.createCell(10).setCellValue(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                row.createCell(11).setCellValue(record.getHandExaminationStatistics().getNoSeizureHandExamination());
                row.createCell(12).setCellValue(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
