package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class DeviceLogWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("设备日志");
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
        tableRowHeader.addNewTableCell().setText("设备");
        tableRowHeader.addNewTableCell().setText("账号");
        tableRowHeader.addNewTableCell().setText("用户");
        tableRowHeader.addNewTableCell().setText("类别");
        tableRowHeader.addNewTableCell().setText("级别");
        tableRowHeader.addNewTableCell().setText("内容");
        tableRowHeader.addNewTableCell().setText("操作时间");

    }

    public static InputStream buildWordDocument(List<SerDevLog> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SerDevLog log = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(log.getId().toString());
                if(log.getDevice() != null) {
                    tableRow.getCell(1).setText(log.getDevice().getDeviceName());
                } else {
                    tableRow.getCell(1).setText("无");
                }
                tableRow.getCell(2).setText(log.getLoginName());
                if(log.getUser() != null) {
                    tableRow.getCell(3).setText(log.getUser().getUserName());
                } else {
                    tableRow.getCell(3).setText("无");
                }
                tableRow.getCell(4).setText(log.getCategory().toString());
                tableRow.getCell(5).setText(log.getLevel().toString());
                tableRow.getCell(6).setText(log.getContent());
                tableRow.getCell(7).setText(formatDate(log.getTime()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
