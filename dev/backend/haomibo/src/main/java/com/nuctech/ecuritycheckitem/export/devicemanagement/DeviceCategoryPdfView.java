/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName FieldManagementPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceCategoryPdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SysDeviceCategory> exportCategoryList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("设备分类"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            Stream.of("序号", "分类编号", "分类", "生效", "上级机构编号", "上级分类", "备注")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });



            for (SysDeviceCategory category : exportCategoryList) {
                table.addCell(category.getCategoryId().toString());
                table.addCell(category.getCategoryNumber());
                table.addCell(category.getCategoryName());
                table.addCell(category.getStatus());
                if(category.getParent() != null) {
                    table.addCell(category.getParent().getCategoryNumber());
                    table.addCell(category.getParent().getCategoryName());
                } else {
                    table.addCell("无");
                    table.addCell("无");
                }
                table.addCell(category.getNote());

            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
