/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceLogExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeviceLogExcelView {

    public static InputStream buildExcelDocument(List<SerDevLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Device-Log");

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName(Constants.EXCEL_HEAD_FONT_NAME);
            font.setFontHeightInPoints(Constants.EXCEL_HEAD_FONT_SIZE);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCellNo = header.createCell(0);
            headerCellNo.setCellValue("序号");
            headerCellNo.setCellStyle(headerStyle);

            Cell headerCellDevice = header.createCell(1);
            headerCellDevice.setCellValue("设备");
            headerCellDevice.setCellStyle(headerStyle);

            Cell headerCellAccount = header.createCell(2);
            headerCellAccount.setCellValue("账号");
            headerCellAccount.setCellStyle(headerStyle);

            Cell headerCellUserName = header.createCell(3);
            headerCellUserName.setCellValue("用户");
            headerCellUserName.setCellStyle(headerStyle);

            Cell headerCellCategory = header.createCell(4);
            headerCellCategory.setCellValue("类别");
            headerCellCategory.setCellStyle(headerStyle);

            Cell headerCellLevel = header.createCell(5);
            headerCellLevel.setCellValue("级别");
            headerCellLevel.setCellStyle(headerStyle);

            Cell headerCellContent = header.createCell(6);
            headerCellContent.setCellValue("内容");
            headerCellContent.setCellStyle(headerStyle);

            Cell headerCellTime = header.createCell(7);
            headerCellTime.setCellValue("操作时间");
            headerCellTime.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");
            for (SerDevLog log : exportLogList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(log.getId().toString());
                if(log.getDevice() != null) {
                    row.createCell(1).setCellValue(log.getDevice().getDeviceName());
                } else {
                    row.createCell(1).setCellValue("");
                }
                row.createCell(2).setCellValue(log.getLoginName());
                if(log.getUser() != null) {
                    row.createCell(3).setCellValue(log.getUser().getUserName());
                } else {
                    row.createCell(3).setCellValue("");
                }
                row.createCell(4).setCellValue(log.getCategory().toString());
                row.createCell(5).setCellValue(log.getLevel().toString());
                row.createCell(6).setCellValue(log.getContent());
                row.createCell(7).setCellValue(dateFormat.format(log.getTime()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
