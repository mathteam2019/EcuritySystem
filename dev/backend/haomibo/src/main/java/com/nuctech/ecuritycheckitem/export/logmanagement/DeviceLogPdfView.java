/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceLogPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
    public static InputStream buildPDFDocument(List<SerDevLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("设备日志"));
            document.add(getTime());
            PdfPTable table = new PdfPTable(8);

            table.setWidthPercentage(100);
            Stream.of("序号", "设备", "账号", "用户", "类别", "级别", "内容", "操作时间")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });


            for (SerDevLog log : exportLogList) {
                table.addCell(log.getId().toString());
                if(log.getDevice() != null) {
                    table.addCell(log.getDevice().getDeviceName());
                } else {
                    table.addCell("");
                }
                table.addCell(log.getLoginName());
                if(log.getUser() != null) {
                    table.addCell(log.getUser().getUserName());
                } else {
                    table.addCell("");
                }

                table.addCell(log.getCategory().toString());
                table.addCell(log.getLevel().toString());
                table.addCell(log.getContent());
                table.addCell(formatDate(log.getTime()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
