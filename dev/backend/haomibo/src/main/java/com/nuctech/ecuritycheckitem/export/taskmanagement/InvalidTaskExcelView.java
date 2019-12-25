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
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class InvalidTaskExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTaskNumber = header.createCell(1);
        headerCellTaskNumber.setCellValue(messageSource.getMessage("TaskNumber", null, currentLocale));

        Cell headerCellWorkMode = header.createCell(2);
        headerCellWorkMode.setCellValue(messageSource.getMessage("WorkMode", null, currentLocale));

        Cell headerCellField = header.createCell(3);
        headerCellField.setCellValue(messageSource.getMessage("Scene", null, currentLocale));

        Cell headerCellScanDevice = header.createCell(4);
        headerCellScanDevice.setCellValue(messageSource.getMessage("ScanDeviceName", null, currentLocale));

        Cell headerCellScanUser = header.createCell(5);
        headerCellScanUser.setCellValue(messageSource.getMessage("ScanUserName", null, currentLocale));

        Cell headerCellScanStartTime = header.createCell(6);
        headerCellScanStartTime.setCellValue(messageSource.getMessage("ScanStartTime", null, currentLocale));

        Cell headerCellScanEndTime = header.createCell(7);
        headerCellScanEndTime.setCellValue(messageSource.getMessage("ScanEndTime", null, currentLocale));

    }


    public static InputStream buildExcelDocument(List<SerTask> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("invalid-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("InvalidTaskTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            for (SerTask task : exportTaskList) {

                Row row = sheet.createRow(counter++);

                row.createCell(0).setCellValue(task.getTaskId());

                row.createCell(1).setCellValue(task.getTaskNumber());

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        row.createCell(2).setCellValue(ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                    } else {
                        row.createCell(2).setCellValue("无");
                    }
                } else {

                }

                if (task.getField() != null) {
                    row.createCell(3).setCellValue(task.getField().getFieldDesignation());
                } else {
                    row.createCell(3).setCellValue("无");
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        row.createCell(4).setCellValue(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        row.createCell(4).setCellValue("无");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        row.createCell(5).setCellValue(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        row.createCell(5).setCellValue("无");
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        row.createCell(6).setCellValue(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        row.createCell(6).setCellValue("无");
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        row.createCell(7).setCellValue(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        row.createCell(7).setCellValue("无");
                    }

                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");
                    row.createCell(6).setCellValue("无");
                    row.createCell(7).setCellValue("无");
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
