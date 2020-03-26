/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HistoryTaskPdfView）
 * 文件名：	HistoryTaskPdfView.java
 * 描述：	HistoryTaskPdfView
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
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class HistoryTaskPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportTaskList
     * @return
     */
    public static InputStream buildPDFDocument(List<HistorySimplifiedForHistoryTableManagement> exportTaskList) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("HistoryTaskTableTitle", null, currentLocale)));
            document.add(getTime());
            PdfPTable table = new PdfPTable(9);

            table.setWidthPercentage(99);
            Stream.of("ID", "TaskNumber", "WorkMode", "TaskResult", "Scene", "ScanDeviceName", "ScanUserName", "ScanStartTime", "ScanEndTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            int number = 0;
            for (HistorySimplifiedForHistoryTableManagement task : exportTaskList) {

                addTableCell(table, String.valueOf(++ number));

                if (task.getTask() != null) {
                    addTableCell(table, task.getTask().getTaskNumber());
                }
                else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getWorkMode() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(task.getWorkMode().getModeName()));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                addTableCell(table, ConstantDictionary.getDataValue(task.getHandTaskResult()));

                if (task.getTask() != null) {
                    if (task.getTask().getField() != null) {
                        if (task.getTask().getField().getFieldDesignation() != null) {
                            addTableCell(table, task.getTask().getField().getFieldDesignation());
                        } else {
                            addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                        }
                    } else {
                        addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    }

                }
                else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }


                if (task.getScanDevice() != null) {
                    addTableCell(table, task.getScanDevice().getDeviceName());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanPointsman() != null) {
                    addTableCell(table, task.getScanPointsman().getUserName());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanStartTime() != null) {
                    addTableCell(table, formatDate(task.getScanStartTime()));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanEndTime() != null) {
                    addTableCell(table, formatDate(task.getScanEndTime()));
                } else {
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
