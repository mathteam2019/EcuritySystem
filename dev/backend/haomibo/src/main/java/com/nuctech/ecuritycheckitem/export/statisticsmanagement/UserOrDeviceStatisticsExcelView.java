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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class UserOrDeviceStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);



        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue("时间段");

        Cell headerCellTotalHandExam = header.createCell(2);
        headerCellTotalHandExam.setCellValue("扫描总量");

        Cell headerCellMissing = header.createCell(3);
        headerCellMissing.setCellValue("无效扫描量");

        Cell headerCellMissingRate = header.createCell(4);
        headerCellMissingRate.setCellValue("无效率");

        Cell headerCellMistake = header.createCell(5);
        headerCellMistake.setCellValue("判图量");

        Cell headerCellMistakeRate = header.createCell(6);
        headerCellMistakeRate.setCellValue("手检量");

        Cell headerCellArtificialJudge = header.createCell(7);
        headerCellArtificialJudge.setCellValue("无嫌疑量");

        Cell headerCellArtificialJudgeMissing = header.createCell(8);
        headerCellArtificialJudgeMissing.setCellValue("无嫌疑率");

        Cell headerCellArtificialJudgeMissingRate = header.createCell(9);
        headerCellArtificialJudgeMissingRate.setCellValue("无查获量");

        Cell headerCellArtificialJudgeMistake = header.createCell(10);
        headerCellArtificialJudgeMistake.setCellValue("无查获率");

        Cell headerCellArtificialJudgeMistakeRate = header.createCell(11);
        headerCellArtificialJudgeMistakeRate.setCellValue("查获量");

        Cell headerCellIntelligenceJudge = header.createCell(12);
        headerCellIntelligenceJudge.setCellValue("查获率");

    }


    public static InputStream buildExcelDocument(TreeMap<Long, TotalStatistics> detailedStatistics, Boolean type) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("statistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);

            if (type) {
                titleCell.setCellValue("人员工时统计");
            }
            else {
                titleCell.setCellValue("设备运行时长统计");
            }
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue( Long.toString(record.getScanStatistics().getTotalScan()));
                row.createCell(3).setCellValue( Long.toString(record.getScanStatistics().getInvalidScan()));
                row.createCell(4).setCellValue( df.format(record.getScanStatistics().getInvalidScanRate()));
                row.createCell(5).setCellValue( Long.toString(record.getJudgeStatistics().getTotalJudge()));
                row.createCell(6).setCellValue( Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                row.createCell(7).setCellValue( df.format(record.getJudgeStatistics().getNoSuspictionJudge()));
                row.createCell(8).setCellValue( df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                row.createCell(9).setCellValue( Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                row.createCell(10).setCellValue( df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));
                row.createCell(11).setCellValue( Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                row.createCell(12).setCellValue( df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
