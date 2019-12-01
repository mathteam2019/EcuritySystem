/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UserExcelView {

    public static InputStream buildExcelDocument(List<SysUser> exportUserList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("User");

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
            headerCellNumber.setCellValue("人员编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellName = header.createCell(2);
            headerCellName.setCellValue("人员");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellGender = header.createCell(3);
            headerCellGender.setCellValue("性别");
            headerCellGender.setCellStyle(headerStyle);

            Cell headerCellStatus = header.createCell(4);
            headerCellStatus.setCellValue("状态");
            headerCellStatus.setCellStyle(headerStyle);

            Cell headerCellCategory = header.createCell(5);
            headerCellCategory.setCellValue("隶属机构");
            headerCellCategory.setCellStyle(headerStyle);

            Cell headerCellAccount = header.createCell(6);
            headerCellAccount.setCellValue("账号");
            headerCellAccount.setCellStyle(headerStyle);



            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUser user : exportUserList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(user.getUserId().toString());
                row.createCell(1).setCellValue(user.getUserNumber());
                row.createCell(2).setCellValue(user.getUserName());
                row.createCell(3).setCellValue(user.getGender());
                row.createCell(4).setCellValue(user.getStatus());
                row.createCell(5).setCellValue(user.getOrg().getOrgName());
                row.createCell(6).setCellValue(user.getUserAccount());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
