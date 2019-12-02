/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName FieldManagementExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.fieldmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FieldManagementExcelView {

    public static InputStream buildExcelDocument(List<SysField> exportFieldList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Field");

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

            Cell headerCellSerial = header.createCell(1);
            headerCellSerial.setCellValue("场地编号");
            headerCellSerial.setCellStyle(headerStyle);

            Cell headerCellDesignation = header.createCell(2);
            headerCellDesignation.setCellValue("场地");
            headerCellDesignation.setCellStyle(headerStyle);

            Cell headerCellStatus = header.createCell(3);
            headerCellStatus.setCellValue("生效");
            headerCellStatus.setCellStyle(headerStyle);

            Cell headerCellParentSerial = header.createCell(4);
            headerCellParentSerial.setCellValue("上级场地编号");
            headerCellParentSerial.setCellStyle(headerStyle);

            Cell headerCellParentDesignation = header.createCell(5);
            headerCellParentDesignation.setCellValue("上级场地");
            headerCellParentDesignation.setCellStyle(headerStyle);

            Cell headerCellLeader = header.createCell(6);
            headerCellLeader.setCellValue("负责人");
            headerCellLeader.setCellStyle(headerStyle);

            Cell headerCellMobile = header.createCell(7);
            headerCellMobile.setCellValue("联系电话");
            headerCellMobile.setCellStyle(headerStyle);

            Cell headerCellNote = header.createCell(8);
            headerCellNote.setCellValue("备注");
            headerCellNote.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysField field : exportFieldList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(field.getFieldId().toString());
                row.createCell(1).setCellValue(field.getFieldSerial());
                row.createCell(2).setCellValue(field.getFieldDesignation());
                row.createCell(3).setCellValue(field.getStatus());
                if(field.getParent() != null) {
                    row.createCell(4).setCellValue(field.getParent().getFieldSerial());
                    row.createCell(5).setCellValue(field.getParent().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(field.getLeader());
                row.createCell(7).setCellValue(field.getMobile());
                row.createCell(8).setCellValue(field.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
