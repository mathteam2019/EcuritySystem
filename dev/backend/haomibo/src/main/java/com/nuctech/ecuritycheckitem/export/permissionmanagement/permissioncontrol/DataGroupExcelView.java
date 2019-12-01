/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName DataGroupExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DataGroupExcelView {

    public static InputStream buildExcelDocument(List<SysDataGroup> exportDataGroupList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DataGroup");

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

            Cell headerCellNumber = header.createCell(1);
            headerCellNumber.setCellValue("数据组编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("数据组");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellRange = header.createCell(3);
            headerCellRange.setCellValue("数据组范围");
            headerCellRange.setCellStyle(headerStyle);




            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysDataGroup dataGroup : exportDataGroupList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(dataGroup.getDataGroupId().toString());
                row.createCell(1).setCellValue(dataGroup.getDataGroupNumber());
                row.createCell(2).setCellValue(dataGroup.getDataGroupName());
                row.createCell(3).setCellValue("");
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
