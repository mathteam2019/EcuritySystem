/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserOrDeviceStatisticsWordView）
 * 文件名：	UserOrDeviceStatisticsWordView.java
 * 描述：	UserOrDeviceStatisticsWordView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.TableWidthType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class UserOrDeviceStatisticsWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     * @param isUserStatOrDeviceStat
     */
    private static void createHeaderPart(XWPFDocument document, boolean isUserStatOrDeviceStat) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        if (isUserStatOrDeviceStat) {
            titleRun.setText(messageSource.getMessage("UserStatisticsTableTitle", null, currentLocale));
        }
        else {
            titleRun.setText(messageSource.getMessage("DeviceStatisticsTableTitle", null, currentLocale));
        }

        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    /**
     * create table header row
     * @param table
     * @param isUserStatOrDeviceStat
     */
    private static void createTableHeader(XWPFTable table, boolean isUserStatOrDeviceStat) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("ID", null, currentLocale));

        if (isUserStatOrDeviceStat) {
            tableRowHeader.addNewTableCell().setText(messageSource.getMessage("UserName", null, currentLocale));
        }
        else {
            tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceName", null, currentLocale));
        }
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalHandExam", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Missing", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("MissingRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Mistake", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("MistakeRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudge", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMissing", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMissingRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMistake", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMistakeRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("IntelligenceJudge", null, currentLocale));
        

    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildWordDocument(TreeMap<Long, TotalStatistics> detailedStatistics, boolean isUserStatOrDeviceStat) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document, isUserStatOrDeviceStat);

            XWPFTable table = document.createTable();

            createTableHeader(table, isUserStatOrDeviceStat);

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(record.getName());
                if (record.getScanStatistics() != null) {
                    tableRow.getCell(2).setText(Long.toString(record.getScanStatistics().getTotalScan()));
                    tableRow.getCell(3).setText(Long.toString(record.getScanStatistics().getInvalidScan()));
                    tableRow.getCell(4).setText(df.format(record.getScanStatistics().getInvalidScanRate()));
                }
                else {
                    tableRow.getCell(2).setText("无");
                    tableRow.getCell(3).setText("无");
                    tableRow.getCell(4).setText("无");
                }

                if (record.getJudgeStatistics() != null) {
                    tableRow.getCell(5).setText(Long.toString(record.getJudgeStatistics().getTotalJudge()));
                }
                else {
                    tableRow.getCell(5).setText("无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    tableRow.getCell(6).setText(Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                }
                else {
                    tableRow.getCell(6);
                }

                if (record.getJudgeStatistics() != null) {
                    tableRow.getCell(7).setText(df.format(record.getJudgeStatistics().getNoSuspictionJudge()));
                    tableRow.getCell(8).setText(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                }
                else {
                    tableRow.getCell(7);
                    tableRow.getCell(8);
                }

                if (record.getHandExaminationStatistics() != null) {
                    tableRow.getCell(9).setText(Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                    tableRow.getCell(10).setText(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));
                    tableRow.getCell(11).setText(Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                    tableRow.getCell(12).setText(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                }
                else {
                    tableRow.getCell(9);
                    tableRow.getCell(10);
                    tableRow.getCell(11);
                    tableRow.getCell(12);
                }


            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
