/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
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
import java.util.ArrayList;
import java.util.List;

public class AssignUserExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellName = header.createCell(1);
        headerCellName.setCellValue("人员");

        Cell headerCellGender = header.createCell(2);
        headerCellGender.setCellValue("性别");

        Cell headerCellAccount = header.createCell(3);
        headerCellAccount.setCellValue("账号");

        Cell headerCellGroup = header.createCell(4);
        headerCellGroup.setCellValue("隶属机构");

        Cell headerCellRole = header.createCell(5);
        headerCellRole.setCellValue("角色");

        Cell headerCellCategory = header.createCell(6);
        headerCellCategory.setCellValue("数据范围");
    }

    public static InputStream buildExcelDocument(List<SysUser> exportUserList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Assign User");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("人员授权");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUser user : exportUserList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(user.getUserId().toString());
                row.createCell(1).setCellValue(user.getUserName());
                row.createCell(2).setCellValue(ConstantDictionary.getDataValue(user.getGender()));
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
                    row.createCell(5).setCellValue("无");
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
