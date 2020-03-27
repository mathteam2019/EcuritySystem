/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignUserExcelView）
 * 文件名：	AssignUserExcelView.java
 * 描述：	AssignUserExcelView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysAssignUser;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AssignUserExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("AssignUser.No", null, currentLocale));

        Cell headerCellName = header.createCell(1);
        headerCellName.setCellValue(messageSource.getMessage("AssignUser.Name", null, currentLocale));

        Cell headerCellGender = header.createCell(2);
        headerCellGender.setCellValue(messageSource.getMessage("AssignUser.Gender", null, currentLocale));

        Cell headerCellAccount = header.createCell(3);
        headerCellAccount.setCellValue(messageSource.getMessage("AssignUser.Account", null, currentLocale));

        Cell headerCellGroup = header.createCell(4);
        headerCellGroup.setCellValue(messageSource.getMessage("AssignUser.Group", null, currentLocale));

        Cell headerCellRole = header.createCell(5);
        headerCellRole.setCellValue(messageSource.getMessage("AssignUser.Role", null, currentLocale));

        Cell headerCellCategory = header.createCell(6);
        headerCellCategory.setCellValue(messageSource.getMessage("AssignUser.Category", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportUserList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysAssignUser> exportUserList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Assign User");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("AssignUser.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            int number = 0;
            for (SysAssignUser user : exportUserList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(++ number));
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
                    row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                row.createCell(6).setCellValue(ConstantDictionary.getDataValue(user.getDataRangeCategory()));

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
