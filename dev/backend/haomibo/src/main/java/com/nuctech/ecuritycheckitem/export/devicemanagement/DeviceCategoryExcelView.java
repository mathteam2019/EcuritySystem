/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceCategoryExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceCategoryExcelView {

    public static InputStream buildExcelDocument(List<SysDeviceCategory> exportCategoryList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceCategory");

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
            headerCellNumber.setCellValue("分类编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("分类");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellStatus = header.createCell(3);
            headerCellStatus.setCellValue("生效");
            headerCellStatus.setCellStyle(headerStyle);

            Cell headerCellParentNumber = header.createCell(4);
            headerCellParentNumber.setCellValue("上级机构编号");
            headerCellParentNumber.setCellStyle(headerStyle);

            Cell headerCellParentName = header.createCell(5);
            headerCellParentName.setCellValue("上级分类");
            headerCellParentName.setCellStyle(headerStyle);

            Cell headerCellNote = header.createCell(6);
            headerCellNote.setCellValue("备注");
            headerCellNote.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysDeviceCategory category : exportCategoryList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(category.getCategoryId().toString());
                row.createCell(1).setCellValue(category.getCategoryNumber());
                row.createCell(2).setCellValue(category.getCategoryName());
                row.createCell(3).setCellValue(category.getStatus());
                if(category.getParent() != null) {
                    row.createCell(4).setCellValue(category.getParent().getCategoryNumber());
                    row.createCell(5).setCellValue(category.getParent().getCategoryName());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(category.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
