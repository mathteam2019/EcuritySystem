package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class InvalidTaskWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("无效任务");
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
        tableRowHeader.addNewTableCell().setText("现场");
        tableRowHeader.addNewTableCell().setText("安检仪");
        tableRowHeader.addNewTableCell().setText("引导员");
        tableRowHeader.addNewTableCell().setText("扫描开始时间");
        tableRowHeader.addNewTableCell().setText("扫描结束时间");

    }

    public static InputStream buildWordDocument(List<SerTask> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportTaskList.size(); i ++) {

                SerTask task = exportTaskList.get(i);

                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(task.getTaskId().toString());
                tableRow.getCell(1).setText(task.getTaskNumber());

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        tableRow.getCell(2).setText(ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                    } else {
                        tableRow.getCell(2).setText("无");
                    }
                } else {

                }

                if (task.getField() != null) {
                    tableRow.getCell(3).setText(task.getField().getFieldDesignation());
                } else {
                    tableRow.getCell(3).setText("无");
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        tableRow.getCell(4).setText(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        tableRow.getCell(4).setText("无");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        tableRow.getCell(5).setText(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        tableRow.getCell(5).setText("无");
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        tableRow.getCell(6).setText(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        tableRow.getCell(6).setText("无");
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        tableRow.getCell(7).setText(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        tableRow.getCell(7).setText("无");
                    }

                } else {
                    tableRow.getCell(4).setText("无");
                    tableRow.getCell(5).setText("无");
                    tableRow.getCell(6).setText("无");
                    tableRow.getCell(7).setText("无");
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
