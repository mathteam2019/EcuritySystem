/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName AccessLogExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
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

public class AccessLogExcelView {

    public static InputStream buildExcelDocument(List<SysAccessLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Access-Log");

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

            Cell headerCellOperateTime = header.createCell(1);
            headerCellOperateTime.setCellValue("访问时间");
            headerCellOperateTime.setCellStyle(headerStyle);

            Cell headerCellAction = header.createCell(2);
            headerCellAction.setCellValue("动作");
            headerCellAction.setCellStyle(headerStyle);

            Cell headerCellClientIp = header.createCell(3);
            headerCellClientIp.setCellValue("访问ip");
            headerCellClientIp.setCellStyle(headerStyle);

            Cell headerCellOperateAccount = header.createCell(4);
            headerCellOperateAccount.setCellValue("访问用户");
            headerCellOperateAccount.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");
            for (SysAccessLog log : exportLogList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(log.getId().toString());
                row.createCell(1).setCellValue(dateFormat.format(log.getOperateTime()));
                row.createCell(2).setCellValue(log.getAction());
                row.createCell(3).setCellValue(log.getClientIp());
                row.createCell(4).setCellValue(log.getOperateAccount());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
