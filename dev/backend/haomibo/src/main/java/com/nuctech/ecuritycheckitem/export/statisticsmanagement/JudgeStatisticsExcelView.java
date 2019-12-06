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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponse;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
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

public class JudgeStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue("时间段");

        Cell headerCellTotalJudge = header.createCell(2);
        headerCellTotalJudge.setCellValue("判图总量");

        Cell headerCellArtificialResult = header.createCell(3);
        headerCellArtificialResult.setCellValue("人工结论量");

        Cell headerCellArtificialResultRate = header.createCell(4);
        headerCellArtificialResultRate.setCellValue("人工结论率");

        Cell headerCellAssignTimoutResult = header.createCell(5);
        headerCellAssignTimoutResult.setCellValue("分派超时结论量");

        Cell headerCellAssignTimeoutResultRate = header.createCell(6);
        headerCellAssignTimeoutResultRate.setCellValue("分派超时结论率");

        Cell headerCellJudgeTimeoutResult = header.createCell(7);
        headerCellJudgeTimeoutResult.setCellValue("判图超时结论量");

        Cell headerCellJudgeTimeoutResultRate = header.createCell(8);
        headerCellJudgeTimeoutResultRate.setCellValue("判图超时结论率");

        Cell headerCellScanResult = header.createCell(9);
        headerCellScanResult.setCellValue("扫描结论量");

        Cell headerCellScanResultRate = header.createCell(10);
        headerCellScanResultRate.setCellValue("扫描结论率");

        Cell headerCellNoSuspicion = header.createCell(11);
        headerCellNoSuspicion.setCellValue("无嫌疑量");

        Cell headerCellNoSuspicionRate = header.createCell(12);
        headerCellNoSuspicionRate.setCellValue("无嫌疑率");

        Cell headerCellSuspicion = header.createCell(13);
        headerCellSuspicion.setCellValue("嫌疑量");

        Cell headerCellSuspicionRate = header.createCell(14);
        headerCellSuspicionRate.setCellValue("嫌疑率");

        Cell headerCellArtificialJudgeDefaultTime = header.createCell(15);
        headerCellArtificialJudgeDefaultTime.setCellValue("人工判图时长阈值");

        Cell headerCellArtificialJudgeAvgTime = header.createCell(16);
        headerCellArtificialJudgeAvgTime.setCellValue("人工判图平均时长");

        Cell headerCellArtificialJudgeMaxTime = header.createCell(17);
        headerCellArtificialJudgeMaxTime.setCellValue("人工判图最高时长");

        Cell headerCellArtificialJudgeMinTime = header.createCell(18);
        headerCellArtificialJudgeMinTime.setCellValue("人工判图最低时长");

    }


    public static InputStream buildExcelDocument(TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("JudgeStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("判图统计");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 0;

            for (Map.Entry<Integer, JudgeStatisticsResponseModel> entry : detailedStatistics.entrySet()) {

                JudgeStatisticsResponseModel record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getTotal());
                row.createCell(3).setCellValue(record.getArtificialResult());
                row.createCell(4).setCellValue(df.format(record.getArtificialResultRate()));
                row.createCell(5).setCellValue(record.getAssignTimeout());
                row.createCell(6).setCellValue(df.format(record.getAssignTimeoutResultRate()));
                row.createCell(7).setCellValue(record.getJudgeTimeout());
                row.createCell(8).setCellValue(df.format(record.getJudgeTimeoutResultRate()));
                row.createCell(9).setCellValue(record.getScanResult());
                row.createCell(10).setCellValue(df.format(record.getScanResultRate()));
                row.createCell(11).setCellValue(record.getNoSuspiction());
                row.createCell(12).setCellValue(df.format(record.getNoSuspictionRate()));
                row.createCell(13).setCellValue(record.getSuspiction());
                row.createCell(14).setCellValue(df.format(record.getSuspictionRate()));
                row.createCell(15).setCellValue(record.getLimitedArtificialDuration());
                row.createCell(16).setCellValue(record.getAvgArtificialJudgeDuration());
                row.createCell(17).setCellValue(record.getMaxArtificialJudgeDuration());
                row.createCell(18).setCellValue(record.getMinArtificialJudgeDuration());


            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
