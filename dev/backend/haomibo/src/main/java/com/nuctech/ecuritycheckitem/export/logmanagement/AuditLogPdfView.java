/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName AuditLogPdfView.java
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
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

public class AuditLogPdfView {
    public static InputStream buildPDFDocument(List<SysAuditLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();
            PdfPTable table = new PdfPTable(9);
            Stream.of("序号", "操作员ID", "客户端ip", "操作对象", "操作", "操作内容", "操作结果", "失败原因代码", "操作时间")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });


            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");

            for (SysAuditLog log : exportLogList) {
                table.addCell(log.getId().toString());
                table.addCell(log.getOperatorId().toString());
                table.addCell(log.getClientIp());
                table.addCell(log.getOperateObject());
                table.addCell(log.getAction());
                table.addCell(log.getOperateContent());
                table.addCell(log.getOperateResult());
                table.addCell(log.getReasonCode());
                table.addCell(dateFormat.format(log.getOperateTime()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
