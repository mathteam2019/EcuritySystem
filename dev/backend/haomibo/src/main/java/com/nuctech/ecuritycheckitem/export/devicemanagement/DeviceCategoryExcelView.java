/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName DeviceCategoryExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DeviceCategoryExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue("分类编号");

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue("分类");

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue("生效");

        Cell headerCellParentNumber = header.createCell(4);
        headerCellParentNumber.setCellValue("上级机构编号");

        Cell headerCellParentName = header.createCell(5);
        headerCellParentName.setCellValue("上级分类");

        Cell headerCellNote = header.createCell(6);
        headerCellNote.setCellValue("备注");
    }

    public static InputStream buildExcelDocument(List<SysDeviceCategory> exportCategoryList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceCategory");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("设备分类");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysDeviceCategory category : exportCategoryList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(category.getCategoryId().toString());
                row.createCell(1).setCellValue(category.getCategoryNumber());
                row.createCell(2).setCellValue(category.getCategoryName());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(category.getStatus()));
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
