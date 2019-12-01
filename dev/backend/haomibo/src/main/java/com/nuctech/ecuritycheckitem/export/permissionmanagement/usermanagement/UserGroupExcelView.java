/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserGroupExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UserGroupExcelView {

    public static InputStream buildExcelDocument(List<SysUserGroup> exportUserGroupList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("UserGroup");

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
            headerCellNumber.setCellValue("人员分组编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("人员组");
            headerCellName.setCellStyle(headerStyle);




            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUserGroup userGroup : exportUserGroupList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(userGroup.getUserGroupId().toString());
                row.createCell(1).setCellValue(userGroup.getGroupNumber());
                row.createCell(2).setCellValue(userGroup.getGroupName());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
