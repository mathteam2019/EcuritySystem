/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeStatisticsExcelView）
 * 文件名：	EvaluateJudgeStatisticsExcelView.java
 * 描述：	EvaluateJudgeStatisticsExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
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

public class EvaluateJudgeStatisticsExcelView extends BaseExcelView {

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

        Cell headerCellIntelligenceJudgeMissing = header.createCell(13);
        headerCellIntelligenceJudgeMissing.setCellValue(messageSource.getMessage("IntelligenceJudgeMissing", null, currentLocale));

        Cell headerCellIntelligenceJudgeMissingRate = header.createCell(14);
        headerCellIntelligenceJudgeMissingRate.setCellValue(messageSource.getMessage("IntelligenceJudgeMissingRate", null, currentLocale));

        Cell headerCellIntelligenceJudgeMistake = header.createCell(15);
        headerCellIntelligenceJudgeMistake.setCellValue(messageSource.getMessage("IntelligenceJudgeMistake", null, currentLocale));

        Cell headerCellIntelligenceJudgeMistakeRate = header.createCell(16);
        headerCellIntelligenceJudgeMistakeRate.setCellValue(messageSource.getMessage("IntelligenceJudgeMistakeRate", null, currentLocale));


    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildExcelDocument(List<EvaluateJudgeResponseModel> detailedStatistics) {

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

            for (EvaluateJudgeResponseModel record: detailedStatistics) {

                Row row = sheet.createRow(counter ++);
                DecimalFormat df = new DecimalFormat("0.00");

                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.getTime());
                row.createCell(2).setCellValue(record.getTotal());
                row.createCell(3).setCellValue(Long.toString(record.getMissingReport()));
                row.createCell(4).setCellValue(df.format(record.getMissingReportRate()));
                row.createCell(5).setCellValue(Long.toString(record.getMistakeReport()));
                row.createCell(6).setCellValue(df.format(record.getMistakeReportRate()));

                row.createCell(7).setCellValue(Long.toString(record.getArtificialJudge()));
                row.createCell(8).setCellValue(Long.toString(record.getArtificialJudgeMissing()));
                row.createCell(9).setCellValue(df.format(record.getArtificialJudgeMissingRate()));
                row.createCell(10).setCellValue(Long.toString(record.getArtificialJudgeMistake()));
                row.createCell(11).setCellValue(df.format(record.getArtificialJudgeMistakeRate()));
                row.createCell(12).setCellValue(Long.toString(record.getIntelligenceJudge()));
                row.createCell(13).setCellValue(Long.toString(record.getIntelligenceJudgeMissing()));
                row.createCell(14).setCellValue(df.format(record.getIntelligenceJudgeMissingRate()));
                row.createCell(15).setCellValue(Long.toString(record.getIntelligenceJudgeMistake()));
                row.createCell(16).setCellValue(df.format(record.getIntelligenceJudgeMistakeRate()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
