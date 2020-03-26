/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ProcessTaskExcelView）
 * 文件名：	ProcessTaskExcelView.java
 * 描述：	ProcessTaskExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ProcessTaskExcelView extends BaseExcelView {

    /**
     * set table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        Cell headerCellTaskNumber = header.createCell(1);
        headerCellTaskNumber.setCellValue(messageSource.getMessage("TaskNumber", null, currentLocale));

        Cell headerCellWorkMode = header.createCell(2);
        headerCellWorkMode.setCellValue(messageSource.getMessage("WorkMode", null, currentLocale));

        Cell headerCellState = header.createCell(3);
        headerCellState.setCellValue(messageSource.getMessage("TaskStatus", null, currentLocale));

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
    }


    public static InputStream buildExcelDocument(List<SerTaskSimplifiedForProcessTableManagement> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("process-task");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("ProcessTaskTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            int number = 0;
            for (SerTaskSimplifiedForProcessTableManagement task : exportTaskList) {

                Row row = sheet.createRow(counter++);

                row.createCell(0).setCellValue(String.valueOf(++ number));

                row.createCell(1).setCellValue(task.getTaskNumber());

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        row.createCell(2).setCellValue(ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                    } else {
                        row.createCell(2).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    }
                } else {

                }

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(task.getTaskStatus()));

                if (task.getField() != null) {
                    row.createCell(4).setCellValue(task.getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        row.createCell(5).setCellValue(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        row.createCell(6).setCellValue(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        row.createCell(6).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        row.createCell(7).setCellValue(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        row.createCell(7).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        row.createCell(8).setCellValue(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        row.createCell(8).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    }


                } else {
                    row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    row.createCell(6).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    row.createCell(7).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    row.createCell(8).setCellValue(messageSource.getMessage("None", null, currentLocale));
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
