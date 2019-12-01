/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName OrganizationExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OrganizationExcelView {

    public static InputStream buildExcelDocument(List<SysOrg> exportOrgList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Organization");

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
            headerCellNumber.setCellValue("机构编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("机构名称");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellStatus = header.createCell(3);
            headerCellStatus.setCellValue("生效");
            headerCellStatus.setCellStyle(headerStyle);

            Cell headerCellParentNumber = header.createCell(4);
            headerCellParentNumber.setCellValue("上级机构编号");
            headerCellParentNumber.setCellStyle(headerStyle);

            Cell headerCellParentName = header.createCell(5);
            headerCellParentName.setCellValue("上级机构");
            headerCellParentName.setCellStyle(headerStyle);

            Cell headerCellLeader = header.createCell(6);
            headerCellLeader.setCellValue("负责人");
            headerCellLeader.setCellStyle(headerStyle);

            Cell headerCellMobile = header.createCell(7);
            headerCellMobile.setCellValue("联系方式");
            headerCellMobile.setCellStyle(headerStyle);

            Cell headerCellNote = header.createCell(8);
            headerCellNote.setCellValue("备注");
            headerCellNote.setCellStyle(headerStyle);


            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysOrg org : exportOrgList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(org.getOrgId().toString());
                row.createCell(1).setCellValue(org.getOrgNumber());
                row.createCell(2).setCellValue(org.getOrgName());
                row.createCell(3).setCellValue(org.getStatus());
                if(org.getParent() != null) {
                    row.createCell(4).setCellValue(org.getParent().getOrgNumber());
                    row.createCell(5).setCellValue(org.getParent().getOrgName());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(org.getLeader());
                row.createCell(7).setCellValue(org.getMobile());
                row.createCell(8).setCellValue(org.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
