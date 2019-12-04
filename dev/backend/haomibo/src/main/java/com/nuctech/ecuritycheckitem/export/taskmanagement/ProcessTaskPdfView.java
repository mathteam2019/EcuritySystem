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

public class ProcessTaskPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(List<SerTask> exportTaskList) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("过程任务"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(16);

            table.setWidthPercentage(100);
            Stream.of("序号", "任务编号", "工作模式", "状态", "现场", "安检仪", "引导员", "扫描开始时间", "扫描结束时间", "判图站", "判图员", "判图开始时间", "判图结束时间", "手检站", "手检员", "手检开始时间")
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

                if (task.getWorkFlow().getWorkMode().getModeName() != null) {
                    table.addCell(task.getWorkFlow().getWorkMode().getModeName());
                } else {
                    table.addCell("");
                }

                table.addCell(task.getTaskStatus());

                if (task.getField().getFieldDesignation() != null) {
                    table.addCell(task.getField().getFieldDesignation());
                } else {
                    table.addCell("");
                }

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
                } else {
                    table.addCell("");
                }

                if (task.getSerScan().getScanEndTime() != null) {
                    table.addCell(formatDate(task.getSerScan().getScanEndTime()));
                } else {
                    table.addCell("");
                }

                if (task.getSerJudgeGraph().getJudgeDevice().getDeviceName() != null) {
                    table.addCell(task.getSerJudgeGraph().getJudgeDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if (task.getSerJudgeGraph().getJudgeUser().getUserName() != null) {
                    table.addCell(task.getSerJudgeGraph().getJudgeUser().getUserName());
                } else {
                    table.addCell("");
                }

                if (task.getSerJudgeGraph().getJudgeStartTime() != null) {
                    table.addCell(formatDate(task.getSerJudgeGraph().getJudgeStartTime()));
                } else {
                    table.addCell("");
                }

                if (task.getSerJudgeGraph().getJudgeEndTime() != null) {
                    table.addCell(formatDate(task.getSerJudgeGraph().getJudgeEndTime()));
                } else {
                    table.addCell("");
                }

                if (task.getSerHandExamination().getHandUser().getUserName() != null) {
                    table.addCell(task.getSerHandExamination().getHandUser().getUserName());
                } else {
                    table.addCell("");
                }

                if (task.getSerHandExamination().getHandDevice().getDeviceName() != null) {
                    table.addCell(task.getSerHandExamination().getHandDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if (task.getSerHandExamination().getHandEndTime() != null) {
                    table.addCell(formatDate(task.getSerHandExamination().getHandEndTime()));
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
