/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PreviewStatisticsExcelView）
 * 文件名：	PreviewStatisticsExcelView.java
 * 描述：	PreviewStatisticsExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class PreviewStatisticsExcelView extends BaseExcelView {

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

        Cell headerCellTotalScan = header.createCell(2);
        headerCellTotalScan.setCellValue(messageSource.getMessage("TotalScan", null, currentLocale));

        Cell headerCellInvalidScans = header.createCell(3);
        headerCellInvalidScans.setCellValue(messageSource.getMessage("ValidScans", null, currentLocale));

        Cell headerInvalidScanRate = header.createCell(4);
        headerInvalidScanRate.setCellValue(messageSource.getMessage("ValidScanRate", null, currentLocale));

        Cell headerCellTotalJudge = header.createCell(5);
        headerCellTotalJudge.setCellValue(messageSource.getMessage("TotalJudge", null, currentLocale));

        Cell headerCellsuspicion = header.createCell(6);
        headerCellsuspicion.setCellValue(messageSource.getMessage("Suspicion", null, currentLocale));

        Cell headerCellScansuspictionRate = header.createCell(7);
        headerCellScansuspictionRate.setCellValue(messageSource.getMessage("SuspicionRate", null, currentLocale));

        Cell headerCellNosuspicion = header.createCell(8);
        headerCellNosuspicion.setCellValue(messageSource.getMessage("Nosuspicion", null, currentLocale));

        Cell headerCellScanNosuspictionRate = header.createCell(9);
        headerCellScanNosuspictionRate.setCellValue(messageSource.getMessage("ScanNosuspictionRate", null, currentLocale));

        Cell headerCellTotalHands = header.createCell(10);
        headerCellTotalHands.setCellValue(messageSource.getMessage("TotalHands", null, currentLocale));

        Cell headerCellSeizure = header.createCell(11);
        headerCellSeizure.setCellValue(messageSource.getMessage("Seizure", null, currentLocale));

        Cell headerCellSeizureRate = header.createCell(12);
        headerCellSeizureRate.setCellValue(messageSource.getMessage("SeizureRate", null, currentLocale));

        Cell headerCellNoSeizure = header.createCell(13);
        headerCellNoSeizure.setCellValue(messageSource.getMessage("NoSeizure", null, currentLocale));

        Cell headerCellNoSeizureRate = header.createCell(14);
        headerCellNoSeizureRate.setCellValue(messageSource.getMessage("NoSeizureRate", null, currentLocale));



    }


    public static InputStream buildExcelDocument(List<TotalStatistics> detailedStatistics) {

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

            long index = 1;
            int counter = 4;

            for (TotalStatistics record : detailedStatistics) {

                Row row = sheet.createRow(counter ++);

                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getScanStatistics().getTotalScan());
                row.createCell(3).setCellValue(record.getScanStatistics().getValidScan());
                row.createCell(4).setCellValue(df.format(record.getScanStatistics().getValidScanRate()));
                row.createCell(5).setCellValue(record.getJudgeStatistics().getTotalJudge());
                row.createCell(6).setCellValue(record.getJudgeStatistics().getSuspictionJudge());
                row.createCell(7).setCellValue(record.getJudgeStatistics().getSuspictionJudgeRate());
                row.createCell(8).setCellValue(record.getJudgeStatistics().getNoSuspictionJudge());
                row.createCell(9).setCellValue(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                row.createCell(10).setCellValue(record.getHandExaminationStatistics().getTotalHandExamination());
                row.createCell(11).setCellValue(record.getHandExaminationStatistics().getSeizureHandExamination());
                row.createCell(12).setCellValue(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                row.createCell(13).setCellValue(record.getHandExaminationStatistics().getNoSeizureHandExamination());
                row.createCell(14).setCellValue(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));




            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
