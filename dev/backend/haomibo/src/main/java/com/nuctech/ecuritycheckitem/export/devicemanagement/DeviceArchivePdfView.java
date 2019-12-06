/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceArchivePdfView.java
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
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerArchive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceArchivePdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SerArchive> exportArchiveList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("档案管理"));
            document.add(getTime());
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            Stream.of("序号", "档案编号", "模板", "生效", "设备分类", "生产厂商", "设备型号")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });



            for (SerArchive archive : exportArchiveList) {
                addTableCell(table, archive.getArchiveId().toString());
                addTableCell(table, archive.getArchivesNumber());


                if(archive.getArchiveTemplate() != null) {
                    addTableCell(table, archive.getArchiveTemplate().getTemplateName());
                } else {
                    addTableCell(table, "无");
                }
                addTableCell(table, ConstantDictionary.getDataValue(archive.getStatus()));
                if(archive.getArchiveTemplate() != null && archive.getArchiveTemplate().getDeviceCategory() != null) {
                    addTableCell(table, archive.getArchiveTemplate().getDeviceCategory().getCategoryName());
                } else {
                    addTableCell(table, "无");
                }
                if(archive.getArchiveTemplate() != null) {
                    addTableCell(table, archive.getArchiveTemplate().getManufacturer());
                    addTableCell(table, archive.getArchiveTemplate().getOriginalModel());
                } else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
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
