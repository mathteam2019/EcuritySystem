/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName FieldManagementExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.fieldmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FieldManagementExcelView extends BaseExcelView {
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellSerial = header.createCell(1);
        headerCellSerial.setCellValue("场地编号");

        Cell headerCellDesignation = header.createCell(2);
        headerCellDesignation.setCellValue("场地");

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue("生效");

        Cell headerCellParentSerial = header.createCell(4);
        headerCellParentSerial.setCellValue("上级场地编号");

        Cell headerCellParentDesignation = header.createCell(5);
        headerCellParentDesignation.setCellValue("上级场地");

        Cell headerCellLeader = header.createCell(6);
        headerCellLeader.setCellValue("负责人");

        Cell headerCellMobile = header.createCell(7);
        headerCellMobile.setCellValue("联系电话");

        Cell headerCellNote = header.createCell(8);
        headerCellNote.setCellValue("备注");
    }


    public static InputStream buildExcelDocument(List<SysField> exportFieldList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Field");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("场地管理");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysField field : exportFieldList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(field.getFieldId().toString());
                row.createCell(1).setCellValue(field.getFieldSerial());
                row.createCell(2).setCellValue(field.getFieldDesignation());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(field.getStatus()));
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
