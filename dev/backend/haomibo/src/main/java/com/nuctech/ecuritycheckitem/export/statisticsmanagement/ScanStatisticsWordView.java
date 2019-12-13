package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class ScanStatisticsWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("扫描统计");
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
        tableRowHeader.getCell(0).setText("序号");
        tableRowHeader.addNewTableCell().setText("时间段");
        tableRowHeader.addNewTableCell().setText("扫描总量");
        tableRowHeader.addNewTableCell().setText("有效扫描量");
        tableRowHeader.addNewTableCell().setText("有效率");
        tableRowHeader.addNewTableCell().setText("无效扫描量");
        tableRowHeader.addNewTableCell().setText("无效率");
        tableRowHeader.addNewTableCell().setText("通过量");
        tableRowHeader.addNewTableCell().setText("通过率");
        tableRowHeader.addNewTableCell().setText("报警量");
        tableRowHeader.addNewTableCell().setText("报警率");
        

    }

    public static InputStream buildWordDocument(TreeMap<Integer, ScanStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getTotalScan()));
                tableRow.getCell(3).setText(Long.toString(record.getValidScan()));
                tableRow.getCell(4).setText(df.format(record.getValidScanRate()));
                tableRow.getCell(5).setText(Long.toString(record.getInvalidScan()));
                tableRow.getCell(6).setText(df.format(record.getInvalidScanRate()));
                tableRow.getCell(7).setText(Long.toString(record.getPassedScan()));
                tableRow.getCell(8).setText(df.format(record.getPassedScanRate()));
                tableRow.getCell(9).setText(Long.toString(record.getAlarmScan()));
                tableRow.getCell(10).setText(df.format(record.getAlarmScanRate()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
