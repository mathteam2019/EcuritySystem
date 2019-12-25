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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
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

public class UserOrDeviceStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet, boolean type) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        if (type) {
            Cell headerCellTime = header.createCell(1);
            headerCellTime.setCellValue(messageSource.getMessage("UserName", null, currentLocale));
        }
        else {
            Cell headerCellTime = header.createCell(1);
            headerCellTime.setCellValue(messageSource.getMessage("DeviceName", null, currentLocale));
        }

        Cell headerCellTotalHandExam = header.createCell(2);
        headerCellTotalHandExam.setCellValue(messageSource.getMessage("TotalHandExam", null, currentLocale));

        Cell headerCellMissing = header.createCell(3);
        headerCellMissing.setCellValue(messageSource.getMessage("Missing", null, currentLocale));

        Cell headerCellMissingRate = header.createCell(4);
        headerCellMissingRate.setCellValue(messageSource.getMessage("MissingRate", null, currentLocale));

        Cell headerCellMistake = header.createCell(5);
        headerCellMistake.setCellValue(messageSource.getMessage("Mistake", null, currentLocale));

        Cell headerCellMistakeRate = header.createCell(6);
        headerCellMistakeRate.setCellValue(messageSource.getMessage("MistakeRate", null, currentLocale));

        Cell headerCellArtificialJudge = header.createCell(7);
        headerCellArtificialJudge.setCellValue(messageSource.getMessage("ArtificialJudge", null, currentLocale));

        Cell headerCellArtificialJudgeMissing = header.createCell(8);
        headerCellArtificialJudgeMissing.setCellValue(messageSource.getMessage("ArtificialJudgeMissing", null, currentLocale));

        Cell headerCellArtificialJudgeMissingRate = header.createCell(9);
        headerCellArtificialJudgeMissingRate.setCellValue(messageSource.getMessage("ArtificialJudgeMissingRate", null, currentLocale));

        Cell headerCellArtificialJudgeMistake = header.createCell(10);
        headerCellArtificialJudgeMistake.setCellValue(messageSource.getMessage("ArtificialJudgeMistake", null, currentLocale));

        Cell headerCellArtificialJudgeMistakeRate = header.createCell(11);
        headerCellArtificialJudgeMistakeRate.setCellValue(messageSource.getMessage("ArtificialJudgeMistakeRate", null, currentLocale));

        Cell headerCellIntelligenceJudge = header.createCell(12);
        headerCellIntelligenceJudge.setCellValue(messageSource.getMessage("IntelligenceJudge", null, currentLocale));

    }


    public static InputStream buildExcelDocument(TreeMap<Long, TotalStatistics> detailedStatistics, Boolean type) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("statistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);

            if (type) {
                titleCell.setCellValue(messageSource.getMessage("UserStatisticsTableTitle", null,currentLocale));
            } else {
                titleCell.setCellValue(messageSource.getMessage("DeviceStatisticsTableTitle", null, currentLocale));
            }
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet, type);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                Row row = sheet.createRow(counter++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index++);
                row.createCell(1).setCellValue(record.getName());
                if (record.getScanStatistics() != null) {
                    row.createCell(2).setCellValue(Long.toString(record.getScanStatistics().getTotalScan()));
                    row.createCell(3).setCellValue(Long.toString(record.getScanStatistics().getInvalidScan()));
                    row.createCell(4).setCellValue(df.format(record.getScanStatistics().getInvalidScanRate()));
                }
                else {
                    row.createCell(2).setCellValue("无");
                    row.createCell(3).setCellValue("无");
                    row.createCell(4).setCellValue("无");
                }

                if (record.getJudgeStatistics() != null) {
                    row.createCell(5).setCellValue(Long.toString(record.getJudgeStatistics().getTotalJudge()));
                }
                else {
                    row.createCell(5).setCellValue("无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    row.createCell(6).setCellValue(Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                }
                else {
                    row.createCell(6);
                }

                if (record.getJudgeStatistics() != null) {
                    row.createCell(7).setCellValue(df.format(record.getJudgeStatistics().getNoSuspictionJudge()));
                    row.createCell(8).setCellValue(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                }
                else {
                    row.createCell(7);
                    row.createCell(8);
                }

                if (record.getHandExaminationStatistics() != null) {
                    row.createCell(9).setCellValue(Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                    row.createCell(10).setCellValue(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));
                    row.createCell(11).setCellValue(Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                    row.createCell(12).setCellValue(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                }
                else {
                    row.createCell(9);
                    row.createCell(10);
                    row.createCell(11);
                    row.createCell(12);
                }

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
