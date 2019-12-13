package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EvaluateJudgeStatisticsWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("毫米波人体查验评价判图统计");
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
        tableRowHeader.addNewTableCell().setText("手检总量");
        tableRowHeader.addNewTableCell().setText("误报总量");
        tableRowHeader.addNewTableCell().setText("误报率");
        tableRowHeader.addNewTableCell().setText("漏报总量");
        tableRowHeader.addNewTableCell().setText("漏报率");
        tableRowHeader.addNewTableCell().setText("手检（人工判图）量");
        tableRowHeader.addNewTableCell().setText("人工判图误报量");
        tableRowHeader.addNewTableCell().setText("人工判图误报率");
        tableRowHeader.addNewTableCell().setText("人工判图漏报量");
        tableRowHeader.addNewTableCell().setText("人工判图漏报率");
        tableRowHeader.addNewTableCell().setText("手检（智能判图）量");
        tableRowHeader.addNewTableCell().setText("智能判图误报量");
        tableRowHeader.addNewTableCell().setText("智能判图误报率");
        tableRowHeader.addNewTableCell().setText("智能判图漏报量");
        tableRowHeader.addNewTableCell().setText("智能判图漏报率");
        

    }

    public static InputStream buildWordDocument(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {

                EvaluateJudgeResponseModel record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getTotal()));
                tableRow.getCell(3).setText(Long.toString(record.getMistakeReport()));
                tableRow.getCell(4).setText(df.format(record.getMistakeReportRate()));
                tableRow.getCell(5).setText(Long.toString(record.getMissingReport()));
                tableRow.getCell(6).setText(df.format(record.getMissingReportRate()));
                tableRow.getCell(7).setText(Long.toString(record.getArtificialJudge()));
                tableRow.getCell(8).setText(Long.toString(record.getArtificialJudgeMistake()));
                tableRow.getCell(9).setText(df.format(record.getArtificialJudgeMistakeRate()));
                tableRow.getCell(10).setText(Long.toString(record.getArtificialJudgeMissing()));
                tableRow.getCell(11).setText(df.format(record.getArtificialJudgeMissingRate()));
                tableRow.getCell(12).setText(Long.toString(record.getIntelligenceJudge()));
                tableRow.getCell(13).setText(df.format(record.getIntelligenceJudgeMistake()));
                tableRow.getCell(14).setText(df.format(record.getIntelligenceJudgeMistakeRate()));
                tableRow.getCell(15).setText(df.format(record.getIntelligenceJudgeMissing()));
                tableRow.getCell(16).setText(df.format(record.getIntelligenceJudgeMissingRate()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
