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
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
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

public class AuditLogExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellOperatorId = header.createCell(1);
        headerCellOperatorId.setCellValue("操作员ID");

        Cell headerCellClientIp = header.createCell(2);
        headerCellClientIp.setCellValue("客户端ip");

        Cell headerCellOperateObject = header.createCell(3);
        headerCellOperateObject.setCellValue("操作对象");

        Cell headerCellAction = header.createCell(4);
        headerCellAction.setCellValue("操作");

        Cell headerCellOperateContent = header.createCell(5);
        headerCellOperateContent.setCellValue("操作内容");

        Cell headerCellOperateResult = header.createCell(6);
        headerCellOperateResult.setCellValue("操作结果");

        Cell headerCellReasonCode = header.createCell(7);
        headerCellReasonCode.setCellValue("失败原因代码");

        Cell headerCellOperateTime = header.createCell(8);
        headerCellOperateTime.setCellValue("操作时间");
    }

    public static InputStream buildExcelDocument(List<SysAuditLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Audit-Log");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("操作日志");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
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
                row.createCell(8).setCellValue(formatDate(log.getOperateTime()));
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
