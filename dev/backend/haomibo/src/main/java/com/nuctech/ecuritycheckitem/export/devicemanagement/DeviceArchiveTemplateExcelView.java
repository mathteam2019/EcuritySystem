/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceArchiveTemplateExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceArchiveTemplateExcelView {

    public static InputStream buildExcelDocument(List<SerArchiveTemplate> exportTemplateList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceArchiveTemplate");

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
            headerCellNumber.setCellValue("模板编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("模板");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellStatus = header.createCell(3);
            headerCellStatus.setCellValue("生效");
            headerCellStatus.setCellStyle(headerStyle);

            Cell headerCellCategory = header.createCell(4);
            headerCellCategory.setCellValue("设备分类");
            headerCellCategory.setCellStyle(headerStyle);

            Cell headerCellManufacturer = header.createCell(5);
            headerCellManufacturer.setCellValue("生产厂商");
            headerCellManufacturer.setCellStyle(headerStyle);

            Cell headerCellOriginalModel = header.createCell(6);
            headerCellOriginalModel.setCellValue("设备型号");
            headerCellOriginalModel.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SerArchiveTemplate template : exportTemplateList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(template.getArchivesTemplateId().toString());
                row.createCell(1).setCellValue(template.getArchivesTemplateNumber());
                row.createCell(2).setCellValue(template.getTemplateName());
                row.createCell(3).setCellValue(template.getStatus());
                if(template.getDeviceCategory() != null) {
                    row.createCell(4).setCellValue(template.getDeviceCategory().getCategoryName());
                } else {
                    row.createCell(4).setCellValue("无");

                }
                row.createCell(5).setCellValue(template.getManufacturer());
                row.createCell(6).setCellValue(template.getOriginalModel());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
