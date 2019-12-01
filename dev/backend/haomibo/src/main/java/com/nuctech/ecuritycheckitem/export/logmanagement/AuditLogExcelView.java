/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName AuditLogExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
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

public class AuditLogExcelView {

    public static InputStream buildExcelDocument(List<SysAuditLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Audit-Log");

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

            Cell headerCellOperatorId = header.createCell(1);
            headerCellOperatorId.setCellValue("操作员ID");
            headerCellOperatorId.setCellStyle(headerStyle);

            Cell headerCellClientIp = header.createCell(2);
            headerCellClientIp.setCellValue("客户端ip");
            headerCellClientIp.setCellStyle(headerStyle);

            Cell headerCellOperateObject = header.createCell(3);
            headerCellOperateObject.setCellValue("操作对象");
            headerCellOperateObject.setCellStyle(headerStyle);

            Cell headerCellAction = header.createCell(4);
            headerCellAction.setCellValue("操作");
            headerCellAction.setCellStyle(headerStyle);

            Cell headerCellOperateContent = header.createCell(5);
            headerCellOperateContent.setCellValue("操作内容");
            headerCellOperateContent.setCellStyle(headerStyle);

            Cell headerCellOperateResult = header.createCell(6);
            headerCellOperateResult.setCellValue("操作结果");
            headerCellOperateResult.setCellStyle(headerStyle);

            Cell headerCellReasonCode = header.createCell(7);
            headerCellReasonCode.setCellValue("失败原因代码");
            headerCellReasonCode.setCellStyle(headerStyle);

            Cell headerCellOperateTime = header.createCell(8);
            headerCellOperateTime.setCellValue("操作时间");
            headerCellOperateTime.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");
            for (SysAuditLog log : exportLogList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(log.getId().toString());
                row.createCell(1).setCellValue(log.getOperatorId().toString());
                row.createCell(2).setCellValue(log.getClientIp());
                row.createCell(3).setCellValue(log.getOperateObject());
                row.createCell(4).setCellValue(log.getAction());
                row.createCell(5).setCellValue(log.getOperateContent());
                row.createCell(6).setCellValue(log.getOperateContent());
                row.createCell(7).setCellValue(log.getReasonCode());
                row.createCell(8).setCellValue(dateFormat.format(log.getOperateTime()));
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
