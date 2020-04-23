/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeStatisticsPdfView）
 * 文件名：	EvaluateJudgeStatisticsPdfView.java
 * 描述：	EvaluateJudgeStatisticsPdfView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */


package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EvaluateJudgeStatisticsWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("EvaluateJudgeStatisticsTableTitle", null, currentLocale));
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
     */
    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("ID", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("StatWidth", null, currentLocale));
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
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("IntelligenceJudgeMissing", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("IntelligenceJudgeMissingRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("IntelligenceJudgeMistake", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("IntelligenceJudgeMistakeRate", null, currentLocale));



    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildWordDocument(List<EvaluateJudgeResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            createHeaderPart(document);
            XWPFTable table = document.createTable();
            createTableHeader(table);

            long index = 1;

            for (EvaluateJudgeResponseModel record: detailedStatistics) {

                XWPFTableRow tableRow = table.createRow();
                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index++));
                tableRow.getCell(1).setText(record.getTime());
                tableRow.getCell(2).setText(Long.toString(record.getTotal()));
                tableRow.getCell(3).setText(Long.toString(record.getMissingReport()));
                tableRow.getCell(4).setText(df.format(record.getMissingReportRate()));
                tableRow.getCell(5).setText(Long.toString(record.getMistakeReport()));
                tableRow.getCell(6).setText(df.format(record.getMistakeReportRate()));


                tableRow.getCell(7).setText(Long.toString(record.getArtificialJudge()));
                tableRow.getCell(8).setText(Long.toString(record.getArtificialJudgeMistake()));
                tableRow.getCell(9).setText(df.format(record.getArtificialJudgeMistakeRate()));
                tableRow.getCell(10).setText(Long.toString(record.getArtificialJudgeMissing()));
                tableRow.getCell(11).setText(df.format(record.getArtificialJudgeMissingRate()));
                tableRow.getCell(12).setText(Long.toString(record.getIntelligenceJudge()));
                tableRow.getCell(13).setText(df.format(record.getIntelligenceJudgeMissing()));
                tableRow.getCell(14).setText(df.format(record.getIntelligenceJudgeMissingRate()));
                tableRow.getCell(15).setText(df.format(record.getIntelligenceJudgeMistake()));
                tableRow.getCell(16).setText(df.format(record.getIntelligenceJudgeMistakeRate()));

            }

            setWidth(table, document);
            document.write(out);
            document.close();
        } catch (Exception e) { }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
