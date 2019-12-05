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
import com.nuctech.ecuritycheckitem.models.db.History;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HistoryTaskExcelView extends BaseExcelView {

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

        Cell headerCellTaskResult = header.createCell(4);
        headerCellWorkMode.setCellValue("任务结论");

        Cell headerCellField = header.createCell(5);
        headerCellField.setCellValue("现场");

        Cell headerCellScanDevice = header.createCell(6);
        headerCellScanDevice.setCellValue("安检仪");

        Cell headerCellScanUser = header.createCell(7);
        headerCellScanUser.setCellValue("引导员");

        Cell headerCellScanStartTime = header.createCell(8);
        headerCellScanStartTime.setCellValue("扫描开始时间");

        Cell headerCellScanEndTime = header.createCell(9);
        headerCellScanEndTime.setCellValue("扫描结束时间");

        Cell headerCellJudgeDevice = header.createCell(10);
        headerCellJudgeDevice.setCellValue("判图站");

        Cell headerCellJudgeUser = header.createCell(11);
        headerCellJudgeUser.setCellValue("判图员");

        Cell headerCellJudgeStartTime = header.createCell(12);
        headerCellJudgeStartTime.setCellValue("判图开始时间");

        Cell headerCellJudgeEndTime = header.createCell(13);
        headerCellJudgeEndTime.setCellValue("判图结束时间");

        Cell headerCellHandExaminationDevice = header.createCell(14);
        headerCellHandExaminationDevice.setCellValue("手检站");

        Cell headerCellHandExaminationUser = header.createCell(15);
        headerCellHandExaminationUser.setCellValue("手检员");

        Cell headerCellStartTime = header.createCell(16);
        headerCellStartTime.setCellValue("手检开始时间");
    }


    public static InputStream buildExcelDocument(List<History> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("history-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("历史任务");
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

                if (task.getTask().getTaskNumber() != null) {
                    row.createCell(1).setCellValue(task.getTask().getTaskNumber());
                }
                else {
                    row.createCell(1).setCellValue("");
                }

                if (task.getScanImage() != null) {
                    row.createCell(2).setCellValue(task.getScanImage().getImageLabel());
                } else {
                    row.createCell(2).setCellValue("");
                }

                if (task.getWorkMode() != null) {
                    row.createCell(3).setCellValue(task.getWorkMode().getModeName());
                } else {
                    row.createCell(3).setCellValue("");
                }

                row.createCell(4).setCellValue(task.getHandTaskResult());

                if (task.getTask().getField().getFieldDesignation() != null) {
                    row.createCell(5).setCellValue(task.getTask().getField().getFieldDesignation());
                } else {
                    row.createCell(5).setCellValue("");
                }

                if (task.getScanDevice() != null) {
                    row.createCell(6).setCellValue(task.getScanDevice().getDeviceName());
                } else {
                    row.createCell(6).setCellValue("");
                }

                if (task.getScanPointsman() != null) {
                    row.createCell(7).setCellValue(task.getScanPointsman().getUserName());
                } else {
                    row.createCell(7).setCellValue("");
                }

                if (task.getScanStartTime() != null) {
                    row.createCell(8).setCellValue(task.getScanStartTime());
                } else {
                    row.createCell(8).setCellValue("");
                }

                if (task.getScanEndTime() != null) {
                    row.createCell(9).setCellValue(task.getScanEndTime());
                } else {
                    row.createCell(9).setCellValue("");
                }

                if (task.getJudgeDevice() != null) {
                    row.createCell(10).setCellValue(task.getJudgeDevice().getDeviceName());
                } else {
                    row.createCell(10).setCellValue("");
                }

                if (task.getJudgeUser() != null) {
                    row.createCell(11).setCellValue(task.getJudgeUser().getUserName());
                } else {
                    row.createCell(11).setCellValue("");
                }

                if (task.getJudgeStartTime() != null) {
                    row.createCell(12).setCellValue(task.getJudgeStartTime());
                } else {
                    row.createCell(12).setCellValue("");
                }

                if (task.getJudgeEndTime() != null) {
                    row.createCell(13).setCellValue(task.getJudgeEndTime());
                } else {
                    row.createCell(13).setCellValue("");
                }

                if (task.getHandUser() != null) {
                    row.createCell(14).setCellValue(task.getHandUser().getUserName());
                } else {
                    row.createCell(14).setCellValue("");
                }

                if (task.getHandDevice() != null) {
                    row.createCell(15).setCellValue(task.getHandDevice().getDeviceName());
                } else {
                    row.createCell(15).setCellValue("");
                }

                if (task.getHandEndTime() != null) {
                    row.createCell(16).setCellValue(task.getHandEndTime());
                } else {
                    row.createCell(16).setCellValue("");
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
