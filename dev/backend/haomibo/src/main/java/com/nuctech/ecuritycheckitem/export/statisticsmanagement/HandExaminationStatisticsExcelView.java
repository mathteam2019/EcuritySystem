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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class HandExaminationStatisticsExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);



        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

        Cell headerCellTotalHandExam = header.createCell(2);
        headerCellTotalHandExam.setCellValue(messageSource.getMessage("TotalHandExam", null, currentLocale));

        Cell headerCellNoSeizure = header.createCell(3);
        headerCellNoSeizure.setCellValue(messageSource.getMessage("NoSeizure", null, currentLocale));

        Cell headerCellNoSeizureRate = header.createCell(4);
        headerCellNoSeizureRate.setCellValue(messageSource.getMessage("NoSeizureRate", null, currentLocale));

        Cell headerCellSeizure = header.createCell(5);
        headerCellSeizure.setCellValue(messageSource.getMessage("Seizure", null, currentLocale));

        Cell headerCellSeizureRate = header.createCell(6);
        headerCellSeizureRate.setCellValue(messageSource.getMessage("SeizureRate", null, currentLocale));

        Cell headerCellHandAvgDuration = header.createCell(7);
        headerCellHandAvgDuration.setCellValue(messageSource.getMessage("HandAvgDuration", null, currentLocale));

        Cell headerCellHandMaxDuration = header.createCell(8);
        headerCellHandMaxDuration.setCellValue(messageSource.getMessage("HandMaxDuration", null, currentLocale));

        Cell headerCellHandMinDuration = header.createCell(9);
        headerCellHandMinDuration.setCellValue(messageSource.getMessage("HandMinDuration", null, currentLocale));

    }


    public static InputStream buildExcelDocument(TreeMap<Integer, HandExaminationResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("handExaminationStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("HandExaminationStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (Map.Entry<Integer, HandExaminationResponseModel> entry : detailedStatistics.entrySet()) {

                HandExaminationResponseModel record = entry.getValue();

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getTotal());
                row.createCell(3).setCellValue(record.getNoSeizure());
                row.createCell(4).setCellValue(df.format(record.getNoSeizureRate()));
                row.createCell(5).setCellValue(record.getSeizure());
                row.createCell(6).setCellValue(df.format(record.getSeizureRate()));
                row.createCell(7).setCellValue(record.getAvgDuration());
                row.createCell(8).setCellValue(record.getMaxDuration());
                row.createCell(9).setCellValue(record.getMinDuration());


            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
