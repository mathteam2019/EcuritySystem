/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.History;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class HistoryTaskExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTaskNumber = header.createCell(1);
        headerCellTaskNumber.setCellValue(messageSource.getMessage("TaskNumber", null, currentLocale));

        Cell headerCellWorkMode = header.createCell(2);
        headerCellWorkMode.setCellValue(messageSource.getMessage("WorkMode", null, currentLocale));

        Cell headerCellTaskResult = header.createCell(3);
        headerCellTaskResult.setCellValue(messageSource.getMessage("TaskResult", null, currentLocale));

        Cell headerCellField = header.createCell(4);
        headerCellField.setCellValue(messageSource.getMessage("Scene", null, currentLocale));

        Cell headerCellScanDevice = header.createCell(5);
        headerCellScanDevice.setCellValue(messageSource.getMessage("ScanDeviceName", null, currentLocale));

        Cell headerCellScanUser = header.createCell(6);
        headerCellScanUser.setCellValue(messageSource.getMessage("ScanUserName", null, currentLocale));

        Cell headerCellScanStartTime = header.createCell(7);
        headerCellScanStartTime.setCellValue(messageSource.getMessage("ScanStartTime", null, currentLocale));

        Cell headerCellScanEndTime = header.createCell(8);
        headerCellScanEndTime.setCellValue(messageSource.getMessage("ScanEndTime", null, currentLocale));

        Cell headerCellJudgeDevice = header.createCell(9);
        headerCellJudgeDevice.setCellValue(messageSource.getMessage("JudgeDeviceName", null, currentLocale));

        Cell headerCellJudgeUser = header.createCell(10);
        headerCellJudgeUser.setCellValue(messageSource.getMessage("JudgeUserName", null, currentLocale));

        Cell headerCellJudgeStartTime = header.createCell(11);
        headerCellJudgeStartTime.setCellValue(messageSource.getMessage("JudgeStartTime", null, currentLocale));

        Cell headerCellJudgeEndTime = header.createCell(12);
        headerCellJudgeEndTime.setCellValue(messageSource.getMessage("JudgeEndTime", null, currentLocale));

        Cell headerCellHandExaminationDevice = header.createCell(13);
        headerCellHandExaminationDevice.setCellValue(messageSource.getMessage("HandExaminationDeviceName", null, currentLocale));

        Cell headerCellHandExaminationUser = header.createCell(14);
        headerCellHandExaminationUser.setCellValue(messageSource.getMessage("HandExaminationUserName", null, currentLocale));

        Cell headerCellStartTime = header.createCell(15);
        headerCellStartTime.setCellValue(messageSource.getMessage("HandExaminationStartTime", null, currentLocale));
    }


    public static InputStream buildExcelDocument(List<History> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("history-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);

            titleCell.setCellValue(messageSource.getMessage("HistoryTaskTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            for (History task : exportTaskList) {

                Row row = sheet.createRow(counter++);

                row.createCell(0).setCellValue(task.getHistoryId());

                if (task.getTask() != null) {
                    if (task.getTask().getTaskNumber() != null) {
                        row.createCell(1).setCellValue(task.getTask().getTaskNumber());
                    }
                    else {
                        row.createCell(1).setCellValue("无");
                    }
                }
                else {
                    row.createCell(1).setCellValue("无");
                }

                if (task.getWorkMode() != null) {
                    row.createCell(2).setCellValue(ConstantDictionary.getDataValue(task.getWorkMode().getModeName()));
                } else {
                    row.createCell(2).setCellValue("无");
                }

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(task.getHandTaskResult()));

                if (task.getTask() != null) {
                    if (task.getTask().getField() != null) {
                        if (task.getTask().getField().getFieldDesignation() != null) {
                            row.createCell(4).setCellValue(task.getTask().getField().getFieldDesignation());
                        } else {
                            row.createCell(4).setCellValue("无");
                        }
                    }
                    else {
                        row.createCell(4).setCellValue("无");
                    }
                }
                else {
                    row.createCell(4).setCellValue("无");
                }


                if (task.getScanDevice() != null) {
                    row.createCell(5).setCellValue(task.getScanDevice().getDeviceName());
                } else {
                    row.createCell(5).setCellValue("无");
                }

                if (task.getScanPointsman() != null) {
                    row.createCell(6).setCellValue(task.getScanPointsman().getUserName());
                } else {
                    row.createCell(6).setCellValue("无");
                }

                if (task.getScanStartTime() != null) {
                    row.createCell(7).setCellValue(formatDate(task.getScanStartTime()));
                } else {
                    row.createCell(7).setCellValue("无");
                }

                if (task.getScanEndTime() != null) {
                    row.createCell(8).setCellValue(formatDate(task.getScanEndTime()));
                } else {
                    row.createCell(8).setCellValue("无");
                }

                if (task.getJudgeDevice() != null) {
                    row.createCell(9).setCellValue(task.getJudgeDevice().getDeviceName());
                } else {
                    row.createCell(9).setCellValue("无");
                }

                if (task.getJudgeUser() != null) {
                    row.createCell(10).setCellValue(task.getJudgeUser().getUserName());
                } else {
                    row.createCell(10).setCellValue("无");
                }

                if (task.getJudgeStartTime() != null) {
                    row.createCell(11).setCellValue(formatDate(task.getJudgeStartTime()));
                } else {
                    row.createCell(11).setCellValue("无");
                }

                if (task.getJudgeEndTime() != null) {
                    row.createCell(12).setCellValue(formatDate(task.getJudgeEndTime()));
                } else {
                    row.createCell(12).setCellValue("无");
                }

                if (task.getHandDevice() != null) {
                    row.createCell(13).setCellValue(task.getHandDevice().getDeviceName());
                } else {
                    row.createCell(13).setCellValue("无");
                }

                if (task.getHandUser() != null) {
                    row.createCell(14).setCellValue(task.getHandUser().getUserName());
                } else {
                    row.createCell(14).setCellValue("无");
                }

                if (task.getHandEndTime() != null) {
                    row.createCell(15).setCellValue(formatDate(task.getHandEndTime()));
                } else {
                    row.createCell(15).setCellValue("无");
                }

            }


            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
