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
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceFieldPdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SysDevice> exportDeviceList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("场地配置"));
            document.add(getTime());
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(100);
            Stream.of("序号", "设备编号", "设备", "设备分类", "场地")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });



            for (SysDevice device : exportDeviceList) {
                addTableCell(table, device.getDeviceId().toString());
                addTableCell(table, device.getDeviceSerial());

                addTableCell(table, device.getDeviceName());
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null &&
                        device.getArchive().getArchiveTemplate().getDeviceCategory() != null) {
                    addTableCell(table, device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryName());
                } else {
                    addTableCell(table, "无");
                }
                if(device.getField() != null) {
                    addTableCell(table, device.getField().getFieldDesignation());
                } else {
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
