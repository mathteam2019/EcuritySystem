/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssignUserExcelView {

    public static InputStream buildExcelDocument(List<SysUser> exportUserList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Assign User");

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

            Cell headerCellName = header.createCell(1);
            headerCellName.setCellValue("人员");
            headerCellName.setCellStyle(headerStyle);

            Cell headerCellGender = header.createCell(2);
            headerCellGender.setCellValue("性别");
            headerCellGender.setCellStyle(headerStyle);

            Cell headerCellAccount = header.createCell(3);
            headerCellAccount.setCellValue("账号");
            headerCellAccount.setCellStyle(headerStyle);

            Cell headerCellGroup = header.createCell(4);
            headerCellGroup.setCellValue("隶属机构");
            headerCellGroup.setCellStyle(headerStyle);

            Cell headerCellRole = header.createCell(5);
            headerCellRole.setCellValue("角色");
            headerCellRole.setCellStyle(headerStyle);

            Cell headerCellCategory = header.createCell(6);
            headerCellCategory.setCellValue("数据范围");
            headerCellCategory.setCellStyle(headerStyle);



            int counter = 1;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUser user : exportUserList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(user.getUserId().toString());
                row.createCell(1).setCellValue(user.getUserName());
                row.createCell(2).setCellValue(user.getGender());
                row.createCell(3).setCellValue(user.getUserAccount());
                row.createCell(4).setCellValue(user.getOrg().getOrgName());
                List<SysRole> sysRoleList = new ArrayList<>();
                user.getRoles().forEach(sysRole -> {
                    sysRoleList.add(sysRole);
                });
                if(sysRoleList.size() > 0) {
                    String str = sysRoleList.get(0).getRoleName();
                    for(int i = 1; i < sysRoleList.size(); i ++) {
                        str = str + ", " + sysRoleList.get(i).getRoleName();
                    }
                    row.createCell(5).setCellValue(str);
                } else {
                    row.createCell(5).setCellValue("");
                }

                row.createCell(6).setCellValue(user.getDataRangeCategory());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
