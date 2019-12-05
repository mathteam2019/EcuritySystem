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
import com.nuctech.ecuritycheckitem.models.db.History;
import com.nuctech.ecuritycheckitem.models.db.SerTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class HistoryTaskPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(List<History> exportTaskList) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("过程任务"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(16);

            table.setWidthPercentage(100);
            Stream.of("序号", "任务编号", "图像", "工作模式", "任务结论", "现场", "安检仪", "引导员", "扫描开始时间", "扫描结束时间", "判图站", "判图员", "判图开始时间", "判图结束时间", "手检站", "手检员", "手检开始时间")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            for (History task : exportTaskList) {

                table.addCell(task.getHistoryId().toString());

                table.addCell(task.getTask().getTaskNumber());

                if (task.getScanImage().getImageLabel() != null) {
                    table.addCell(task.getScanImage().getImageLabel());
                } else {
                    table.addCell("");
                }


                if (task.getWorkMode().getModeName() != null) {
                    table.addCell(task.getWorkMode().getModeName());
                } else {
                    table.addCell("");
                }

                table.addCell(task.getHandTaskResult());

                if (task.getTask().getField().getFieldDesignation() != null) {
                    table.addCell(task.getTask().getField().getFieldDesignation());
                } else {
                    table.addCell("");
                }

                if (task.getScanDevice() != null) {
                    table.addCell(task.getScanDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if (task.getScanPointsman() != null) {
                    table.addCell(task.getScanPointsman().getUserName());
                } else {
                    table.addCell("");
                }

                if (task.getScanStartTime() != null) {
                    table.addCell(formatDate(task.getScanStartTime()));
                } else {
                    table.addCell("");
                }

                if (task.getScanEndTime() != null) {
                    table.addCell(formatDate(task.getScanEndTime()));
                } else {
                    table.addCell("");
                }

                if (task.getJudgeDevice() != null) {
                    table.addCell(task.getJudgeDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if (task.getJudgeUser() != null) {
                    table.addCell(task.getJudgeUser().getUserName());
                } else {
                    table.addCell("");
                }

                if (task.getJudgeStartTime() != null) {
                    table.addCell(formatDate(task.getJudgeStartTime()));
                } else {
                    table.addCell("");
                }

                if (task.getJudgeEndTime() != null) {
                    table.addCell(formatDate(task.getJudgeEndTime()));
                } else {
                    table.addCell("");
                }

                if (task.getHandUser() != null) {
                    table.addCell(task.getHandUser().getUserName());
                } else {
                    table.addCell("");
                }

                if (task.getHandDevice() != null) {
                    table.addCell(task.getHandDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if (task.getHandEndTime() != null) {
                    table.addCell(formatDate(task.getHandEndTime()));
                } else {
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
