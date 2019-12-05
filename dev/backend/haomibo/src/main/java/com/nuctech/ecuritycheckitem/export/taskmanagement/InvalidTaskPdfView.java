/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName OrganizationPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class InvalidTaskPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(List<SerTask> exportTaskList) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("过程任务"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(9);

            table.setWidthPercentage(100);
            Stream.of("序号", "任务编号", "图像", "工作模式", "现场", "安检仪", "引导员", "扫描开始时间", "扫描结束时间")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            for (SerTask task : exportTaskList) {

                table.addCell(task.getTaskId().toString());

                table.addCell(task.getTaskNumber());

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanImage() != null) {
                        table.addCell(task.getSerScan().getScanImage().getImageLabel());
                    }
                    else {
                        table.addCell("");
                    }
                }
                else {
                    table.addCell("");
                }

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        table.addCell(task.getWorkFlow().getWorkMode().getModeName());
                    } else {
                        table.addCell("");
                    }
                } else {
                    table.addCell("");
                }

                if (task.getField() != null) {
                    table.addCell(task.getField().getFieldDesignation());
                } else {
                    table.addCell("");
                }

                if (task.getSerScan() != null) {

                    if (task.getSerScan().getScanDevice() != null) {
                        table.addCell(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        table.addCell("");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        table.addCell(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        table.addCell("");
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        table.addCell(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        table.addCell("");
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        table.addCell(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        table.addCell("");
                    }



                } else {
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
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
