package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class HandExaminationStatisticsWordView extends BaseWordView {

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
        tableRowHeader.addNewTableCell().setText("手检总量");
        tableRowHeader.addNewTableCell().setText("无查获量");
        tableRowHeader.addNewTableCell().setText("无查获率");
        tableRowHeader.addNewTableCell().setText("查获");
        tableRowHeader.addNewTableCell().setText("查获率");
        tableRowHeader.addNewTableCell().setText("手检平均时长");
        tableRowHeader.addNewTableCell().setText("手检最高时长");
        tableRowHeader.addNewTableCell().setText("手检最低时长");
        

    }

    public static InputStream buildWordDocument(TreeMap<Integer, HandExaminationResponseModel> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, HandExaminationResponseModel> entry : detailedStatistics.entrySet()) {

                HandExaminationResponseModel record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getTotal()));
                tableRow.getCell(3).setText(Long.toString((record.getNoSeizure())));
                tableRow.getCell(4).setText(df.format(record.getNoSeizureRate()));
                tableRow.getCell(5).setText(Long.toString((record.getSeizure())));
                tableRow.getCell(6).setText(df.format(record.getSeizureRate()));
                tableRow.getCell(7).setText(Double.toString(record.getAvgDuration()));
                tableRow.getCell(8).setText(Double.toString(record.getMaxDuration()));
                tableRow.getCell(9).setText(Double.toString(record.getMinDuration()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
