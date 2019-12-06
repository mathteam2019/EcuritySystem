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
import org.apache.poi.ss.usermodel.*;
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
        headerCellNo.setCellValue("序号");

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue("时间段");

        Cell headerCellTotalHandExam = header.createCell(2);
        headerCellTotalHandExam.setCellValue("手检总量");

        Cell headerCellMissing = header.createCell(3);
        headerCellMissing.setCellValue("误报总量");

        Cell headerCellMissingRate = header.createCell(4);
        headerCellMissingRate.setCellValue("误报率");

        Cell headerCellMistake = header.createCell(5);
        headerCellMistake.setCellValue("漏报总量");

        Cell headerCellMistakeRate = header.createCell(6);
        headerCellMistakeRate.setCellValue("漏报率");

        Cell headerCellArtificialJudge = header.createCell(7);
        headerCellArtificialJudge.setCellValue("手检（人工判图）量");

        Cell headerCellArtificialJudgeMissing = header.createCell(8);
        headerCellArtificialJudgeMissing.setCellValue("人工判图误报量");

        Cell headerCellArtificialJudgeMissingRate = header.createCell(9);
        headerCellArtificialJudgeMissingRate.setCellValue("人工判图误报率");

        Cell headerCellArtificialJudgeMistake = header.createCell(8);
        headerCellArtificialJudgeMistake.setCellValue("人工判图漏报量");

        Cell headerCellArtificialJudgeMistakeRate = header.createCell(9);
        headerCellArtificialJudgeMistakeRate.setCellValue("人工判图漏报率");

        Cell headerCellIntelligenceJudge = header.createCell(8);
        headerCellIntelligenceJudge.setCellValue("手检（智能判图）量");

        Cell headerCellIntelligenceJudgeMistake = header.createCell(9);
        headerCellIntelligenceJudgeMistake.setCellValue("智能判图误报量");

        Cell headerCellIntelligenceJudgeMistakeRate = header.createCell(8);
        headerCellIntelligenceJudgeMistakeRate.setCellValue("智能判图误报率");

        Cell headerCellIntelligenceJudgeMissing = header.createCell(9);
        headerCellIntelligenceJudgeMissing.setCellValue("智能判图漏报量");

        Cell headerCellIntelligenceJudgeMissingRate = header.createCell(8);
        headerCellIntelligenceJudgeMissingRate.setCellValue("智能判图漏报率");


    }


    public static InputStream buildExcelDocument(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("handExaminationStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("毫米波人体查验评价判图统计");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 0;

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
