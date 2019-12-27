/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceLogPdfView）
 * 文件名：	DeviceLogPdfView.java
 * 描述：	DeviceLogPdfView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

public class DeviceLogPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportLogList
     * @return
     */
    public static InputStream buildPDFDocument(List<SerDevLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle(messageSource.getMessage("DeviceLog.Title", null, currentLocale)));
            document.add(getTime());
            PdfPTable table = new PdfPTable(8);

            table.setWidthPercentage(100);
            Stream.of("DeviceLog.No", "DeviceLog.Device", "DeviceLog.Account", "DeviceLog.UserName", "DeviceLog.Category", "DeviceLog.Level", "DeviceLog.Content", "DeviceLog.Time")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            for (SerDevLog log : exportLogList) {
                addTableCell(table, log.getId().toString());
                if(log.getDevice() != null) {
                    addTableCell(table, log.getDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }
                addTableCell(table, log.getLoginName());
                if(log.getUser() != null) {
                    addTableCell(table, log.getUser().getUserName());
                } else {
                    addTableCell(table, "无");
                }

                addTableCell(table, log.getCategory().toString());
                addTableCell(table, log.getLevel().toString());
                addTableCell(table, log.getContent());
                addTableCell(table, formatDate(log.getTime()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
