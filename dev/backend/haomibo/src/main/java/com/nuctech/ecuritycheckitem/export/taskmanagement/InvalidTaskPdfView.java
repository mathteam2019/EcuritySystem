/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（InvalidTaskPdfView）
 * 文件名：	InvalidTaskPdfView.java
 * 描述：	InvalidTaskPdfView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForInvalidTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class InvalidTaskPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportTaskList
     * @return
     */
    public static InputStream buildPDFDocument(List<HistorySimplifiedForInvalidTableManagement> exportTaskList) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("InvalidTaskTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(8);

            table.setWidthPercentage(99);
            Stream.of("ID", "TaskNumber", "WorkMode", "Scene", "ScanDeviceName", "ScanUserName", "ScanStartTime", "ScanEndTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            int number = 0;
            for (HistorySimplifiedForInvalidTableManagement task : exportTaskList) {

                addTableCell(table, String.valueOf(++ number));

                addTableCell(table, task.getTaskNumber());

                if (task.getModeName()!= null) {
                    addTableCell(table, ConstantDictionary.getDataValue(task.getModeName()));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getFieldDesignation() != null) {
                    addTableCell(table, task.getFieldDesignation());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }



                if (task.getScanDeviceName() != null) {
                    addTableCell(table, task.getScanDeviceName());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanPointsManName() != null) {
                    addTableCell(table, task.getScanPointsManName());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanStartTime() != null) {
                    addTableCell(table, formatDate(task.getScanStartTime()));
                }
                else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanEndTime() != null) {
                    addTableCell(table, formatDate(task.getScanEndTime()));
                }
                else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
