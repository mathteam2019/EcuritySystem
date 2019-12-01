/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName AccessLogPdfView.java
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
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

public class AccessLogPdfView {
    public static InputStream buildPDFDocument(List<SysAccessLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();
            PdfPTable table = new PdfPTable(5);
            Stream.of("序号", "访问时间", "动作", "访问ip", "访问用户")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });


            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");

            for (SysAccessLog log : exportLogList) {
                table.addCell(log.getId().toString());
                table.addCell(dateFormat.format(log.getOperateTime()));
                table.addCell(log.getAction());
                table.addCell(log.getClientIp());
                table.addCell(log.getOperateAccount());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
