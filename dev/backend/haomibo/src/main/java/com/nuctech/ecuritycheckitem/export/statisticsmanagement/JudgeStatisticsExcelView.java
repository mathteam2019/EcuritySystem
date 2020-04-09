/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JudgeStatisticsExcelView）
 * 文件名：	JudgeStatisticsExcelView.java
 * 描述：	JudgeStatisticsExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */


package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class JudgeStatisticsExcelView extends BaseExcelView {

    /**
     * set table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

        Cell headerCellTotalJudge = header.createCell(2);
        headerCellTotalJudge.setCellValue(messageSource.getMessage("TotalJudge", null, currentLocale));

        Cell headerCellArtificialResult = header.createCell(3);
        headerCellArtificialResult.setCellValue(messageSource.getMessage("ArtificialResult", null, currentLocale));

        Cell headerCellArtificialResultRate = header.createCell(4);
        headerCellArtificialResultRate.setCellValue(messageSource.getMessage("ArtificialResultRate", null, currentLocale));

        Cell headerCellAssignTimoutResult = header.createCell(5);
        headerCellAssignTimoutResult.setCellValue(messageSource.getMessage("AssignTimoutResult", null, currentLocale));

        Cell headerCellAssignTimeoutResultRate = header.createCell(6);
        headerCellAssignTimeoutResultRate.setCellValue(messageSource.getMessage("AssignTimeoutResultRate", null, currentLocale));

        Cell headerCellJudgeTimeoutResult = header.createCell(7);
        headerCellJudgeTimeoutResult.setCellValue(messageSource.getMessage("JudgeTimeoutResult", null, currentLocale));

        Cell headerCellJudgeTimeoutResultRate = header.createCell(8);
        headerCellJudgeTimeoutResultRate.setCellValue(messageSource.getMessage("JudgeTimeoutResultRate", null, currentLocale));

        Cell headerCellScanResult = header.createCell(9);
        headerCellScanResult.setCellValue(messageSource.getMessage("AtrResult", null, currentLocale));

        Cell headerCellScanResultRate = header.createCell(10);
        headerCellScanResultRate.setCellValue(messageSource.getMessage("AtrResultRate", null, currentLocale));

        Cell headerCellNoSuspicion = header.createCell(11);
        headerCellNoSuspicion.setCellValue(messageSource.getMessage("NoSuspicion", null, currentLocale));

        Cell headerCellNoSuspicionRate = header.createCell(12);
        headerCellNoSuspicionRate.setCellValue(messageSource.getMessage("NoSuspicionRate", null, currentLocale));

        Cell headerCellSuspicion = header.createCell(13);
        headerCellSuspicion.setCellValue(messageSource.getMessage("Suspicion", null, currentLocale));

        Cell headerCellSuspicionRate = header.createCell(14);
        headerCellSuspicionRate.setCellValue(messageSource.getMessage("SuspicionRate", null, currentLocale));

        Cell headerCellArtificialJudgeDefaultTime = header.createCell(15);
        headerCellArtificialJudgeDefaultTime.setCellValue(messageSource.getMessage("ArtificialJudgeDefaultTime", null, currentLocale));

        Cell headerCellArtificialJudgeAvgTime = header.createCell(16);
        headerCellArtificialJudgeAvgTime.setCellValue(messageSource.getMessage("ArtificialJudgeAvgTime", null, currentLocale));

        Cell headerCellArtificialJudgeMaxTime = header.createCell(17);
        headerCellArtificialJudgeMaxTime.setCellValue(messageSource.getMessage("ArtificialJudgeMaxTime", null, currentLocale));

        Cell headerCellArtificialJudgeMinTime = header.createCell(18);
        headerCellArtificialJudgeMinTime.setCellValue(messageSource.getMessage("ArtificialJudgeMinTime", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildExcelDocument(TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("JudgeStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("JudgeStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

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
                row.createCell(9).setCellValue(record.getAtrResult());
                row.createCell(10).setCellValue(df.format(record.getAtrResultRate()));
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
