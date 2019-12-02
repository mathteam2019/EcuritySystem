/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DevicePdfView.java
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
import com.nuctech.ecuritycheckitem.models.db.SysDevice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DevicePdfView {
    public static InputStream buildPDFDocument(List<SysDevice> exportDeviceList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(7);
            Stream.of("序号", "设备编号", "模板", "生效", "设备分类", "生产厂商", "设备型号")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });



            for (SysDevice device : exportDeviceList) {
                table.addCell(device.getDeviceId().toString());
                table.addCell(device.getDeviceSerial());


                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    table.addCell(device.getArchive().getArchiveTemplate().getTemplateName());
                } else {
                    table.addCell("无");
                }
                table.addCell(device.getStatus());
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null &&
                        device.getArchive().getArchiveTemplate().getDeviceCategory() != null) {
                    table.addCell(device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryName());
                } else {
                    table.addCell("无");
                }
                if(device.getArchive() != null &&  device.getArchive().getArchiveTemplate() != null) {
                    table.addCell(device.getArchive().getArchiveTemplate().getManufacturer());
                    table.addCell(device.getArchive().getArchiveTemplate().getOriginalModel());
                } else {
                    table.addCell("无");
                    table.addCell("无");
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
