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
            document.add(getTitle(messageSource.getMessage("HistoryTaskTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(16);

            table.setWidthPercentage(100);
            Stream.of("ID", "TaskNumber", "WorkMode", "TaskResult", "Scene", "ScanDeviceName", "ScanUserName", "ScanStartTime", "ScanEndTime", "JudgeDeviceName", "JudgeUserName", "JudgeStartTime", "JudgeEndTime", "HandExaminationDeviceName", "HandExaminationUserName", "HandExaminationStartTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            for (History task : exportTaskList) {

                addTableCell(table, task.getHistoryId().toString());

                if (task.getTask() != null) {
                    addTableCell(table, task.getTask().getTaskNumber());
                }
                else {
                    addTableCell(table, "无");
                }

//                if (task.getScanImage() != null) {
//                    if (task.getScanImage().getImageLabel() != null) {
//                        addTableCell(table, task.getScanImage().getImageLabel());
//                    } else {
//                        addTableCell(table, "无");
//                    }
//                }
//                else {
//                    addTableCell(table, "无");
//                }


                if (task.getWorkMode() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(task.getWorkMode().getModeName()));
                } else {
                    addTableCell(table, "无");
                }

                addTableCell(table, ConstantDictionary.getDataValue(task.getHandTaskResult()));

                if (task.getTask() != null) {
                    if (task.getTask().getField() != null) {
                        if (task.getTask().getField().getFieldDesignation() != null) {
                            addTableCell(table, task.getTask().getField().getFieldDesignation());
                        } else {
                            addTableCell(table, "无");
                        }
                    } else {
                        addTableCell(table, "无");
                    }

                }
                else {
                    addTableCell(table, "无");
                }


                if (task.getScanDevice() != null) {
                    addTableCell(table, task.getScanDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getScanPointsman() != null) {
                    addTableCell(table, task.getScanPointsman().getUserName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getScanStartTime() != null) {
                    addTableCell(table, formatDate(task.getScanStartTime()));
                } else {
                    addTableCell(table, "无");
                }

                if (task.getScanEndTime() != null) {
                    addTableCell(table, formatDate(task.getScanEndTime()));
                } else {
                    addTableCell(table, "无");
                }

                if (task.getJudgeDevice() != null) {
                    addTableCell(table, task.getJudgeDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getJudgeUser() != null) {
                    addTableCell(table, task.getJudgeUser().getUserName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getJudgeStartTime() != null) {
                    addTableCell(table, formatDate(task.getJudgeStartTime()));
                } else {
                    addTableCell(table, "无");
                }

                if (task.getJudgeEndTime() != null) {
                    addTableCell(table, formatDate(task.getJudgeEndTime()));
                } else {
                    addTableCell(table, "无");
                }



                if (task.getHandDevice() != null) {
                    addTableCell(table, task.getHandDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getHandUser() != null) {
                    addTableCell(table, task.getHandUser().getUserName());
                } else {
                    addTableCell(table, "无");
                }

                if (task.getHandEndTime() != null) {
                    addTableCell(table, formatDate(task.getHandEndTime()));
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
