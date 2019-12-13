package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class SuspictionHandgoodsStatisticsWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("毫米波人体查验手检统计");
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

        SuspicionHandgoodsStatisticsController.handGoodsIDList
                .forEach(columnTitle -> {
                    tableRowHeader.addNewTableCell().setText(columnTitle);
                });

    }

    public static InputStream buildWordDocument(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(record.get("time").toString());

                for (int i = 0; i < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); i ++) {

                    tableRow.getCell(i + 2).setText(record.get(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(i)).toString());

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
