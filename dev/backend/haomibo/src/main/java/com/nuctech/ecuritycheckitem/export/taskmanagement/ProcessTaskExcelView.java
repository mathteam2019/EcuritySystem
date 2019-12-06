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
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProcessTaskExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellTaskNumber = header.createCell(1);
        headerCellTaskNumber.setCellValue("任务编号");

        Cell headerCellWorkMode = header.createCell(2);
        headerCellWorkMode.setCellValue("工作模式");

        Cell headerCellState = header.createCell(3);
        headerCellState.setCellValue("状态");

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

        Cell headerCellJudgeDevice = header.createCell(9);
        headerCellJudgeDevice.setCellValue("判图站");

        Cell headerCellJudgeUser = header.createCell(10);
        headerCellJudgeUser.setCellValue("判图员");

        Cell headerCellJudgeStartTime = header.createCell(11);
        headerCellJudgeStartTime.setCellValue("判图开始时间");

        Cell headerCellJudgeEndTime = header.createCell(12);
        headerCellJudgeEndTime.setCellValue("判图结束时间");

        Cell headerCellHandExaminationDevice = header.createCell(13);
        headerCellHandExaminationDevice.setCellValue("手检站");

        Cell headerCellHandExaminationUser = header.createCell(14);
        headerCellHandExaminationUser.setCellValue("手检员");

        Cell headerCellStartTime = header.createCell(15);
        headerCellStartTime.setCellValue("手检开始时间");
    }


    public static InputStream buildExcelDocument(List<SerTask> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("process-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("过程任务");
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
                        row.createCell(2).setCellValue(task.getWorkFlow().getWorkMode().getModeName());
                    } else {
                        row.createCell(2).setCellValue("无");
                    }
                } else {

                }

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(task.getTaskStatus()));

                if (task.getField() != null) {
                    row.createCell(4).setCellValue(task.getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("无");
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        row.createCell(5).setCellValue(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        row.createCell(5).setCellValue("无");
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        row.createCell(6).setCellValue(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        row.createCell(6).setCellValue("无");
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        row.createCell(7).setCellValue(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        row.createCell(7).setCellValue("无");
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        row.createCell(8).setCellValue(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        row.createCell(8).setCellValue("无");
                    }


                } else {
                    row.createCell(5).setCellValue("无");
                    row.createCell(6).setCellValue("无");
                    row.createCell(7).setCellValue("无");
                    row.createCell(8).setCellValue("无");
                }

                if (task.getSerJudgeGraph() != null) {
                    if (task.getSerJudgeGraph().getJudgeDevice() != null) {
                        row.createCell(9).setCellValue(task.getSerJudgeGraph().getJudgeDevice().getDeviceName());
                    } else {
                        row.createCell(9).setCellValue("无");
                    }

                    if (task.getSerJudgeGraph().getJudgeUser() != null) {
                        row.createCell(10).setCellValue(task.getSerJudgeGraph().getJudgeUser().getUserName());
                    } else {
                        row.createCell(10).setCellValue("无");
                    }

                    if (task.getSerJudgeGraph().getJudgeStartTime() != null) {
                        row.createCell(11).setCellValue(formatDate(task.getSerJudgeGraph().getJudgeStartTime()));
                    }
                    else {
                        row.createCell(11).setCellValue("无");
                    }

                    if (task.getSerJudgeGraph().getJudgeEndTime() != null) {
                        row.createCell(12).setCellValue(formatDate(task.getSerJudgeGraph().getJudgeEndTime()));
                    }
                    else {
                        row.createCell(12).setCellValue("无");
                    }


                }
                else {

                    row.createCell(9).setCellValue("无");
                    row.createCell(10).setCellValue("无");
                    row.createCell(11).setCellValue("无");
                    row.createCell(12).setCellValue("无");
                }

                if(task.getSerHandExamination() != null) {
                    if (task.getSerHandExamination().getHandUser() != null) {
                        row.createCell(13).setCellValue(task.getSerHandExamination().getHandUser().getUserName());
                    } else {
                        row.createCell(13).setCellValue("无");
                    }

                    if (task.getSerHandExamination().getHandDevice() != null) {
                        row.createCell(14).setCellValue(task.getSerHandExamination().getHandDevice().getDeviceName());
                    } else {
                        row.createCell(14).setCellValue("无");
                    }

                    if (task.getSerHandExamination().getHandEndTime() != null) {
                        row.createCell(15).setCellValue(formatDate(task.getSerHandExamination().getHandEndTime()));
                    }
                    else {
                        row.createCell(15).setCellValue("无");
                    }
                }
                else {

                    row.createCell(13).setCellValue("无");
                    row.createCell(14).setCellValue("无");
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
