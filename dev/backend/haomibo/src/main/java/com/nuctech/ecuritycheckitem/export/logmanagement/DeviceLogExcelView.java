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
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
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

public class DeviceLogExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellDevice = header.createCell(1);
        headerCellDevice.setCellValue("设备");

        Cell headerCellAccount = header.createCell(2);
        headerCellAccount.setCellValue("账号");

        Cell headerCellUserName = header.createCell(3);
        headerCellUserName.setCellValue("用户");

        Cell headerCellCategory = header.createCell(4);
        headerCellCategory.setCellValue("类别");

        Cell headerCellLevel = header.createCell(5);
        headerCellLevel.setCellValue("级别");

        Cell headerCellContent = header.createCell(6);
        headerCellContent.setCellValue("内容");

        Cell headerCellTime = header.createCell(7);
        headerCellTime.setCellValue("操作时间");
    }

    public static InputStream buildExcelDocument(List<SerDevLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Device-Log");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("设备日志");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
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
                row.createCell(7).setCellValue(formatDate(log.getTime()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
