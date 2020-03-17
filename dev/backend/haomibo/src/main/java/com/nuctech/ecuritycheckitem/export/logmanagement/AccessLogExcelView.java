/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AccessLogExcelView）
 * 文件名：	AccessLogExcelView.java
 * 描述：	AccessLogExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.es.EsSysAccessLog;
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

public class AccessLogExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("AccessLog.No", null, currentLocale));

        Cell headerCellOperateAccount = header.createCell(1);
        headerCellOperateAccount.setCellValue(messageSource.getMessage("AccessLog.OperateAccount", null, currentLocale));

        Cell headerCellOperateUser = header.createCell(2);
        headerCellOperateUser.setCellValue(messageSource.getMessage("AccessLog.OperateUser", null, currentLocale));

        Cell headerCellClientIp = header.createCell(3);
        headerCellClientIp.setCellValue(messageSource.getMessage("AccessLog.ClientIp", null, currentLocale));

        Cell headerCellAction = header.createCell(4);
        headerCellAction.setCellValue(messageSource.getMessage("AccessLog.Action", null, currentLocale));

        Cell headerCellOperateResult = header.createCell(5);
        headerCellOperateResult.setCellValue(messageSource.getMessage("AccessLog.OperateResult", null, currentLocale));

        Cell headerCellReasonCode = header.createCell(6);
        headerCellReasonCode.setCellValue(messageSource.getMessage("AccessLog.ReasonCode", null, currentLocale));

        Cell headerCellOperateTime = header.createCell(7);
        headerCellOperateTime.setCellValue(messageSource.getMessage("AccessLog.OperateTime", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportLogList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysAccessLog> exportLogList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Access-Log");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("AccessLog.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysAccessLog log : exportLogList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(log.getId().toString());
                row.createCell(1).setCellValue(log.getOperateAccount());
                row.createCell(2).setCellValue(log.getUser().getUserName());
                row.createCell(3).setCellValue(log.getClientIp());
                row.createCell(4).setCellValue(log.getAction());
                row.createCell(5).setCellValue(log.getOperateResult());
                row.createCell(6).setCellValue(log.getReasonCode());
                row.createCell(7).setCellValue(formatDate(log.getOperateTime()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
