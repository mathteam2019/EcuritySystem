package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class KnowledgeDealPendingWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("待审批案例");
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
        tableRowHeader.addNewTableCell().setText("任务编号");
        tableRowHeader.addNewTableCell().setText("工作模式");
        tableRowHeader.addNewTableCell().setText("任务结论");
        tableRowHeader.addNewTableCell().setText("现场");
        tableRowHeader.addNewTableCell().setText("安检仪");
        tableRowHeader.addNewTableCell().setText("判图站");
        tableRowHeader.addNewTableCell().setText("手检站");
        tableRowHeader.addNewTableCell().setText("查获物品");

    }

    public static InputStream buildWordDocument(List<SerKnowledgeCaseDeal> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SerKnowledgeCaseDeal deal = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(deal.getCaseDealId().toString());
                if(deal.getTask() != null) {
                    tableRow.getCell(1).setText(deal.getTask().getTaskNumber());
                } else {
                    tableRow.getCell(1).setText("无");
                }

                if(deal.getWorkMode() != null) {
                    tableRow.getCell(2).setText(ConstantDictionary.getDataValue(deal.getWorkMode().getModeName()));
                } else {
                    tableRow.getCell(2).setText("无");
                }

                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    tableRow.getCell(4).setText(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    tableRow.getCell(4).setText("无");
                }

                if(deal.getScanDevice() != null) {
                    tableRow.getCell(5).setText(deal.getScanDevice().getDeviceName());
                } else {
                    tableRow.getCell(5).setText("无");
                }

                if(deal.getJudgeDevice() != null) {
                    tableRow.getCell(6).setText(deal.getJudgeDevice().getDeviceName());
                } else {
                    tableRow.getCell(6).setText("无");
                }

                if(deal.getHandDevice() != null) {
                    tableRow.getCell(7).setText(deal.getHandDevice().getDeviceName());
                } else {
                    tableRow.getCell(7).setText("无");
                }
                tableRow.getCell(8).setText(deal.getHandResult());

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
