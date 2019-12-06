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
        headerCellNo.setCellValue("序号");

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue("时间段");

        Cell headerCellTotalScan = header.createCell(2);
        headerCellTotalScan.setCellValue("扫描总量");

        Cell invalidScans = header.createCell(3);
        invalidScans.setCellValue("无效扫描量");

        Cell headerInvalidScanRate = header.createCell(4);
        headerInvalidScanRate.setCellValue("无效率");

        Cell headerCellTotalJudge = header.createCell(5);
        headerCellTotalJudge.setCellValue("判图量");

        Cell headerCellTotalHands = header.createCell(6);
        headerCellTotalHands.setCellValue("手检量");

        Cell headerCellNosuspicion = header.createCell(7);
        headerCellNosuspicion.setCellValue("无嫌疑量");

        Cell headerCellScanNosuspictionRate = header.createCell(8);
        headerCellScanNosuspictionRate.setCellValue("无嫌疑率");

        Cell headerCellNoSeizure = header.createCell(9);
        headerCellNoSeizure.setCellValue("无查获量");

        Cell headerCellNoSeizureRate = header.createCell(10);
        headerCellNoSeizureRate.setCellValue("无查获率");

        Cell headerCellSeizure = header.createCell(11);
        headerCellSeizure.setCellValue("查获量");

        Cell headerCellSeizureRate = header.createCell(12);
        headerCellSeizureRate.setCellValue("查获率");

    }


    public static InputStream buildExcelDocument(TreeMap<Long, TotalStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Preview-Statistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("统计预览");
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
