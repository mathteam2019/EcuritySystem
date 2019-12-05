/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName FieldManagementPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.fieldmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class FieldManagementPdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SysField> exportFieldList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("场地管理"));
            document.add(getTime());
            PdfPTable table = new PdfPTable(9);

            table.setWidthPercentage(100);

            Stream.of("序号", "场地编号", "场地", "生效", "上级场地编号", "上级场地", "负责人", "联系电话", "备注")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });



            for (SysField field : exportFieldList) {
                table.addCell(field.getFieldId().toString());
                table.addCell(field.getFieldSerial());
                table.addCell(field.getFieldDesignation());
                table.addCell(field.getStatus());
                if(field.getParent() != null) {
                    table.addCell(field.getParent().getFieldSerial());
                    table.addCell(field.getParent().getFieldDesignation());
                } else {
                    table.addCell("无");
                    table.addCell("无");
                }
                table.addCell(field.getLeader());
                table.addCell(field.getMobile());
                table.addCell(field.getNote());

            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
