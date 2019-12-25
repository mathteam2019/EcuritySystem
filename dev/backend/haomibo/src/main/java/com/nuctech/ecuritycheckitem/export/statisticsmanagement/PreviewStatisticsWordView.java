package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
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

public class PreviewStatisticsWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("PreviewStatisticsTableTitle", null, currentLocale));
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
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalScan", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("InvalidScans", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("InvalidScanRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalJudge", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalHands", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Nosuspicion", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanNosuspictionRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("NoSeizure", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("NoSeizureRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Seizure", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("SeizureRate", null, currentLocale));
        

    }

    public static InputStream buildWordDocument(TreeMap<Long, TotalStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getScanStatistics().getTotalScan()));
                tableRow.getCell(3).setText(Long.toString(record.getScanStatistics().getInvalidScan()));
                tableRow.getCell(4).setText(df.format(record.getScanStatistics().getInvalidScanRate()));
                tableRow.getCell(5).setText(Long.toString(record.getJudgeStatistics().getTotalJudge()));
                tableRow.getCell(6).setText(Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                tableRow.getCell(7).setText(Long.toString(record.getJudgeStatistics().getNoSuspictionJudge()));
                tableRow.getCell(8).setText(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                tableRow.getCell(9).setText(Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                tableRow.getCell(10).setText(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                tableRow.getCell(11).setText(Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                tableRow.getCell(12).setText(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
