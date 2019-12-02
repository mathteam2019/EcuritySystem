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
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceExcelView {

    public static InputStream buildExcelDocument(List<SysDevice> exportDeviceList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceArchive");

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

            Cell headerCellDevice = header.createCell(1);
            headerCellDevice.setCellValue("设备编号");
            headerCellDevice.setCellStyle(headerStyle);

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
            for (SysDevice device : exportDeviceList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(device.getDeviceId().toString());
                row.createCell(1).setCellValue(device.getDeviceSerial());
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    row.createCell(2).setCellValue(device.getArchive().getArchiveTemplate().getTemplateName());
                } else {
                    row.createCell(2).setCellValue("无");
                }
                row.createCell(3).setCellValue(device.getStatus());
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null
                        && device.getArchive().getArchiveTemplate().getCategory() != null) {
                    row.createCell(4).setCellValue(device.getArchive().getArchiveTemplate().getCategory().getCategoryName());
                } else {
                    row.createCell(4).setCellValue("无");
                }
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    row.createCell(5).setCellValue(device.getArchive().getArchiveTemplate().getManufacturer());
                    row.createCell(6).setCellValue(device.getArchive().getArchiveTemplate().getOriginalModel());
                } else {
                    row.createCell(5).setCellValue("无");
                    row.createCell(6).setCellValue("无");
                }

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
