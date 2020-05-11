/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（InvalidTaskExcelView）
 * 文件名：	InvalidTaskExcelView.java
 * 描述：	InvalidTaskExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForInvalidTableManagement;
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

public class InvalidTaskExcelView extends BaseExcelView {

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

        Cell headerCellWorkingMode = header.createCell(2);
        headerCellTaskNumber.setCellValue(messageSource.getMessage("WorkMode", null, currentLocale));


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


    public static InputStream buildExcelDocument(List<HistorySimplifiedForInvalidTableManagement> exportTaskList) {

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

            int number = 0;
            for (HistorySimplifiedForInvalidTableManagement task : exportTaskList) {

                Row row = sheet.createRow(counter++);

                row.createCell(0).setCellValue(String.valueOf(++ number));

                row.createCell(1).setCellValue(task.getTaskNumber());
                if (task.getModeName()!= null) {
                    row.createCell(2).setCellValue(ConstantDictionary.getDataValue(task.getModeName()));
                } else {
                    row.createCell(2).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }




                if (task.getFieldDesignation() != null) {
                    row.createCell(3).setCellValue(task.getFieldDesignation());
                } else {
                    row.createCell(3).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanDeviceName() != null) {
                    row.createCell(4).setCellValue(task.getScanDeviceName());
                } else {
                    row.createCell(4).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanPointsManName() != null) {
                    row.createCell(5).setCellValue(task.getScanPointsManName());
                } else {
                    row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanStartTime() != null) {
                    row.createCell(6).setCellValue(formatDate(task.getScanStartTime()));
                }
                else {
                    row.createCell(6).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanEndTime() != null) {
                    row.createCell(7).setCellValue(formatDate(task.getScanEndTime()));
                }
                else {
                    row.createCell(7).setCellValue(messageSource.getMessage("None", null, currentLocale));
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
