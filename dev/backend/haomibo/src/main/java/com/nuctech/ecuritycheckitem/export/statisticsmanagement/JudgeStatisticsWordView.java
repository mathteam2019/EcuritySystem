package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class JudgeStatisticsWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("JudgeStatisticsTableTitle", null, currentLocale));
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("ID", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("StatWidth", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalJudge", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialResultRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AssignTimoutResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AssignTimeoutResultRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeTimeoutResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeTimeoutResultRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanResultRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("NoSuspicion", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("NoSuspicionRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Suspicion", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("SuspicionRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeDefaultTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeAvgTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMaxTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ArtificialJudgeMinTime", null, currentLocale));
        

    }

    public static InputStream buildWordDocument(TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, JudgeStatisticsResponseModel> entry : detailedStatistics.entrySet()) {

                JudgeStatisticsResponseModel record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getTotal()));
                tableRow.getCell(3).setText(Long.toString(record.getArtificialResult()));
                tableRow.getCell(4).setText(df.format(record.getArtificialResultRate()));
                tableRow.getCell(5).setText(Long.toString(record.getAssignTimeout()));
                tableRow.getCell(6).setText(df.format(record.getAssignTimeoutResultRate()));
                tableRow.getCell(7).setText(Long.toString(record.getJudgeTimeout()));
                tableRow.getCell(8).setText(df.format(record.getJudgeTimeoutResultRate()));
                tableRow.getCell(9).setText(Long.toString(record.getScanResult()));
                tableRow.getCell(10).setText(df.format(record.getScanResultRate()));
                tableRow.getCell(11).setText(Long.toString(record.getNoSuspiction()));
                tableRow.getCell(12).setText(df.format(record.getNoSuspictionRate()));
                tableRow.getCell(13).setText(Long.toString(record.getSuspiction()));
                tableRow.getCell(14).setText(df.format(record.getSuspictionRate()));
                tableRow.getCell(15).setText(Double.toString(record.getLimitedArtificialDuration()));
                tableRow.getCell(16).setText(Double.toString(record.getAvgArtificialJudgeDuration()));
                tableRow.getCell(17).setText(Double.toString(record.getMaxArtificialJudgeDuration()));
                tableRow.getCell(18).setText(Double.toString(record.getMinArtificialJudgeDuration()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
