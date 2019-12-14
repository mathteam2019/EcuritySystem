package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class AuditLogWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("操作日志");
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
        tableRowHeader.addNewTableCell().setText("操作员ID");
        tableRowHeader.addNewTableCell().setText("客户端ip");
        tableRowHeader.addNewTableCell().setText("操作对象");
        tableRowHeader.addNewTableCell().setText("操作");
        tableRowHeader.addNewTableCell().setText("操作内容");
        tableRowHeader.addNewTableCell().setText("操作结果");
        tableRowHeader.addNewTableCell().setText("失败原因代码");
        tableRowHeader.addNewTableCell().setText("操作时间");

    }

    public static InputStream buildWordDocument(List<SysAuditLog> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SysAuditLog log = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(log.getId().toString());
                tableRow.getCell(1).setText(log.getOperatorId().toString());
                tableRow.getCell(2).setText(log.getClientIp());
                tableRow.getCell(3).setText(log.getOperateObject());
                tableRow.getCell(4).setText(log.getAction());
                tableRow.getCell(5).setText(log.getOperateContent());
                tableRow.getCell(6).setText(log.getOperateContent());
                tableRow.getCell(7).setText(log.getReasonCode());
                tableRow.getCell(8).setText(formatDate(log.getOperateTime()));

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
