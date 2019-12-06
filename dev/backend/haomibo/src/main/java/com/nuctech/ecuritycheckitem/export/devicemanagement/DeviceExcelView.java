/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceArchiveTemplateExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellDevice = header.createCell(1);
        headerCellDevice.setCellValue("设备编号");

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue("模板");

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue("生效");

        Cell headerCellCategory = header.createCell(4);
        headerCellCategory.setCellValue("设备分类");

        Cell headerCellManufacturer = header.createCell(5);
        headerCellManufacturer.setCellValue("生产厂商");

        Cell headerCellOriginalModel = header.createCell(6);
        headerCellOriginalModel.setCellValue("设备型号");
    }

    public static InputStream buildExcelDocument(List<SysDevice> exportDeviceList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceArchive");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("设备管理");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

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
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(device.getStatus()));
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null
                        && device.getArchive().getArchiveTemplate().getDeviceCategory() != null) {
                    row.createCell(4).setCellValue(device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryName());
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
