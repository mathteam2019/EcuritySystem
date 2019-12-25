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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
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

public class EvaluateJudgeStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

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

        Cell headerCellIntelligenceJudgeMistake = header.createCell(13);
        headerCellIntelligenceJudgeMistake.setCellValue(messageSource.getMessage("IntelligenceJudgeMistake", null, currentLocale));

        Cell headerCellIntelligenceJudgeMistakeRate = header.createCell(14);
        headerCellIntelligenceJudgeMistakeRate.setCellValue(messageSource.getMessage("IntelligenceJudgeMistakeRate", null, currentLocale));

        Cell headerCellIntelligenceJudgeMissing = header.createCell(15);
        headerCellIntelligenceJudgeMissing.setCellValue(messageSource.getMessage("IntelligenceJudgeMissing", null, currentLocale));

        Cell headerCellIntelligenceJudgeMissingRate = header.createCell(16);
        headerCellIntelligenceJudgeMissingRate.setCellValue(messageSource.getMessage("IntelligenceJudgeMissingRate", null, currentLocale));


    }


    public static InputStream buildExcelDocument(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("handExaminationStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("EvaluateJudgeStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {

                EvaluateJudgeResponseModel record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getTotal());
                row.createCell(3).setCellValue(Long.toString(record.getMistakeReport()));
                row.createCell(4).setCellValue(df.format(record.getMistakeReportRate()));
                row.createCell(5).setCellValue(Long.toString(record.getMissingReport()));
                row.createCell(6).setCellValue(df.format(record.getMissingReportRate()));
                row.createCell(7).setCellValue(Long.toString(record.getArtificialJudge()));
                row.createCell(8).setCellValue(Long.toString(record.getArtificialJudgeMistake()));
                row.createCell(9).setCellValue(df.format(record.getArtificialJudgeMistakeRate()));
                row.createCell(10).setCellValue(Long.toString(record.getArtificialJudgeMissing()));
                row.createCell(11).setCellValue(df.format(record.getArtificialJudgeMissingRate()));
                row.createCell(12).setCellValue(Long.toString(record.getIntelligenceJudge()));
                row.createCell(13).setCellValue(df.format(record.getIntelligenceJudgeMistake()));
                row.createCell(14).setCellValue(df.format(record.getIntelligenceJudgeMistakeRate()));
                row.createCell(15).setCellValue(df.format(record.getIntelligenceJudgeMissing()));
                row.createCell(16).setCellValue(df.format(record.getIntelligenceJudgeMissingRate()));


            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
