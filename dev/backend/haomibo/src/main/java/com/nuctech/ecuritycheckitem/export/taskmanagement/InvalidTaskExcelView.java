/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import org.apache.poi.ss.usermodel.*;
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
        headerCellNo.setCellValue("序号");

        Cell headerCellTaskNumber = header.createCell(1);
        headerCellTaskNumber.setCellValue("任务编号");

        Cell headerCellScanImage = header.createCell(2);
        headerCellScanImage.setCellValue("图像");

        Cell headerCellWorkMode = header.createCell(3);
        headerCellWorkMode.setCellValue("工作模式");

        Cell headerCellField = header.createCell(4);
        headerCellField.setCellValue("现场");

        Cell headerCellScanDevice = header.createCell(5);
        headerCellScanDevice.setCellValue("安检仪");

        Cell headerCellScanUser = header.createCell(6);
        headerCellScanUser.setCellValue("引导员");

        Cell headerCellScanStartTime = header.createCell(7);
        headerCellScanStartTime.setCellValue("扫描开始时间");

        Cell headerCellScanEndTime = header.createCell(8);
        headerCellScanEndTime.setCellValue("扫描结束时间");

    }


    public static InputStream buildExcelDocument(List<SerTask> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("process-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("无效任务");
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

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanImage() != null) {
                        row.createCell(2).setCellValue(task.getSerScan().getScanImage().getImageLabel());
                    }
                    else {
                        row.createCell(2).setCellValue("");
                    }
                }
                else {
                    row.createCell(2).setCellValue("");
                }

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        row.createCell(3).setCellValue(task.getWorkFlow().getWorkMode().getModeName());
                    } else {
                        row.createCell(3).setCellValue("");
                    }
                } else {

                }

                if (task.getField() != null) {
                    row.createCell(4).setCellValue(task.getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("");
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        row.createCell(5).setCellValue(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        row.createCell(5).setCellValue("");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        row.createCell(6).setCellValue(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        row.createCell(6).setCellValue("");
                    }

                    row.createCell(7).setCellValue(task.getSerScan().getScanStartTime());
                    row.createCell(8).setCellValue(task.getSerScan().getScanEndTime());

                } else {
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue("");
                    row.createCell(7).setCellValue("");
                    row.createCell(8).setCellValue("");
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
