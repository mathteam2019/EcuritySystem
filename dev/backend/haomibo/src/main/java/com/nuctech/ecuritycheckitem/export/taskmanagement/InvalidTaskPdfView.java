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
    public static InputStream buildPDFDocument(List<SerTaskSimplifiedForProcessTableManagement> exportTaskList) {

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
            for (SerTaskSimplifiedForProcessTableManagement task : exportTaskList) {

                addTableCell(table, String.valueOf(++ number));

                addTableCell(table, task.getTaskNumber());

                if (task.getWorkFlow() != null && task.getWorkFlow().getWorkMode()!= null) {
                    addTableCell(table, ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getField() != null) {
                    addTableCell(table, task.getField().getFieldDesignation());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getSerScan() != null) {

                    if (task.getSerScan().getScanDevice() != null) {
                        addTableCell(table, task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        addTableCell(table, task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        addTableCell(table, formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        addTableCell(table, formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    }



                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
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
