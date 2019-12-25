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
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
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
            document.add(getTitle(messageSource.getMessage("ProcessTaskTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(16);

            table.setWidthPercentage(100);
            Stream.of("ID", "TaskNumber", "WorkMode", "TaskStatus", "Scene", "ScanDeviceName", "ScanUserName", "ScanStartTime", "ScanEndTime", "JudgeDeviceName", "JudgeUserName", "JudgeStartTime", "JudgeEndTime", "HandExaminationDeviceName", "HandExaminationUserName", "HandExaminationStartTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            for (SerTask task : exportTaskList) {

                addTableCell(table, task.getTaskId().toString());

                addTableCell(table, task.getTaskNumber());

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        addTableCell(table, ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                    } else {
                        addTableCell(table, "无");
                    }
                } else {
                    addTableCell(table, "无");
                }

                addTableCell(table, ConstantDictionary.getDataValue(task.getTaskStatus()));

                if (task.getField() != null) {
                    addTableCell(table, task.getField().getFieldDesignation());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getSerScan() != null) {

                    if (task.getSerScan().getScanDevice() != null) {
                        addTableCell(table, task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        addTableCell(table, "无");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        addTableCell(table, task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        addTableCell(table, "无");
                    }

                    addTableCell(table, formatDate(task.getSerScan().getScanStartTime()));
                    addTableCell(table, formatDate(task.getSerScan().getScanEndTime()));
                } else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                if (task.getSerJudgeGraph() != null) {
                    if (task.getSerJudgeGraph().getJudgeDevice() != null) {
                        addTableCell(table, task.getSerJudgeGraph().getJudgeDevice().getDeviceName());
                    } else {
                        addTableCell(table, "无");
                    }

                    if (task.getSerJudgeGraph().getJudgeUser() != null) {
                        addTableCell(table, task.getSerJudgeGraph().getJudgeUser().getUserName());
                    } else {
                        addTableCell(table, "无");
                    }

                    addTableCell(table, formatDate(task.getSerJudgeGraph().getJudgeStartTime()));
                    addTableCell(table, formatDate(task.getSerJudgeGraph().getJudgeEndTime()));

                } else {

                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");

                }

                if (task.getSerHandExamination() != null) {



                    if (task.getSerHandExamination().getHandDevice() != null) {
                        addTableCell(table, task.getSerHandExamination().getHandDevice().getDeviceName());
                    } else {
                        addTableCell(table, "无");
                    }

                    if (task.getSerHandExamination().getHandUser() != null) {
                        addTableCell(table, task.getSerHandExamination().getHandUser().getUserName());
                    } else {
                        addTableCell(table, "无");
                    }

                    addTableCell(table, formatDate(task.getSerHandExamination().getHandEndTime()));

                }
                else {

                    addTableCell(table, "无");
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
