/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryExcelView）
 * 文件名：	DeviceCategoryExcelView.java
 * 描述：	DeviceCategoryExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
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
import java.util.List;

public class AuditLogWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("AuditLog.Title", null, currentLocale));
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    /**
     * create table header row
     * @param table
     */
    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("AuditLog.No", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.OperateAccount", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.UserName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.ClientIp", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.Action", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.OperateObject", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.OperateResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.ReasonCode", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AuditLog.OperateTime", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param exportList
     * @return
     */
    public static InputStream buildWordDocument(List<SysAuditLog> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            createHeaderPart(document);
            XWPFTable table = document.createTable();
            createTableHeader(table);

            int number = 0;
            for (Integer i = 0; i < exportList.size(); i ++) {

                SysAuditLog log = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(String.valueOf(++ number));
                tableRow.getCell(1).setText(log.getOperateAccount());
                tableRow.getCell(2).setText(log.getUser().getUserName());
                tableRow.getCell(3).setText(log.getClientIp());
                tableRow.getCell(4).setText(log.getAction());
                tableRow.getCell(5).setText(log.getOperateObject());
                tableRow.getCell(6).setText(ConstantDictionary.getDataValue(log.getOperateResult()));
                tableRow.getCell(7).setText(log.getReasonCode());
                tableRow.getCell(8).setText(formatDate(log.getOperateTime()));

            }

            setWidth(table, document);
            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
