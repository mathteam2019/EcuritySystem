/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuditLogExcelView）
 * 文件名：	AuditLogExcelView.java
 * 描述：	AuditLogExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
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

public class AuditLogExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("AuditLog.No", null, currentLocale));

        Cell headerCellOperateAccount = header.createCell(1);
        headerCellOperateAccount.setCellValue(messageSource.getMessage("AuditLog.OperateAccount", null, currentLocale));

        Cell headerCellUserName = header.createCell(2);
        headerCellUserName.setCellValue(messageSource.getMessage("AuditLog.UserName", null, currentLocale));

        Cell headerCellClientIp = header.createCell(3);
        headerCellClientIp.setCellValue(messageSource.getMessage("AuditLog.ClientIp", null, currentLocale));

        Cell headerCellAction = header.createCell(4);
        headerCellAction.setCellValue(messageSource.getMessage("AuditLog.Action", null, currentLocale));

        Cell headerCellOperateObject = header.createCell(5);
        headerCellOperateObject.setCellValue(messageSource.getMessage("AuditLog.OperateObject", null, currentLocale));

        Cell headerCellOperateResult = header.createCell(6);
        headerCellOperateResult.setCellValue(messageSource.getMessage("AuditLog.OperateResult", null, currentLocale));

        Cell headerCellReasonCode = header.createCell(7);
        headerCellReasonCode.setCellValue(messageSource.getMessage("AuditLog.ReasonCode", null, currentLocale));

        Cell headerCellOperateTime = header.createCell(8);
        headerCellOperateTime.setCellValue(messageSource.getMessage("AuditLog.OperateTime", null, currentLocale));
    }

    /**
     * create title paragraph
     * @param exportLogList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysAuditLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Audit-Log");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("AuditLog.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            int number = 0;
            for (SysAuditLog log : exportLogList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(++ number));
                row.createCell(1).setCellValue(log.getOperateAccount());
                row.createCell(2).setCellValue(log.getUser().getUserName());
                row.createCell(3).setCellValue(log.getClientIp());
                row.createCell(4).setCellValue(log.getAction());
                row.createCell(5).setCellValue(log.getOperateObject());
                row.createCell(6).setCellValue(ConstantDictionary.getDataValue(log.getOperateResult()));
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
