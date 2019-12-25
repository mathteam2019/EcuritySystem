/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignUserGroupExcelView）
 * 文件名：	AssignUserGroupExcelView.java
 * 描述：	AssignUserGroupExcelView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
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

public class AssignUserGroupExcelView  extends BaseExcelView {

    /**
     * build inputstream of data to be exported
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");


        Cell headerCellName = header.createCell(1);
        headerCellName.setCellValue("人员组");

        Cell headerCellUser = header.createCell(2);
        headerCellUser.setCellValue("组员");

        Cell headerCellRole = header.createCell(3);
        headerCellRole.setCellValue("角色");

        Cell headerCellCategory = header.createCell(4);
        headerCellCategory.setCellValue("数据范围");
    }

    /**
     * build inputstream of data to be exported
     * @param exportUserGroupList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysUserGroup> exportUserGroupList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("AssignUserGroup");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("人员组授权");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUserGroup userGroup : exportUserGroupList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(userGroup.getUserGroupId().toString());
                row.createCell(1).setCellValue(userGroup.getGroupName());
                String strMember = "";
                List<SysUser> sysUserList = new ArrayList<>();
                userGroup.getUsers().forEach(sysUser -> {
                    sysUserList.add(sysUser);
                });
                if(sysUserList.size() > 0) {
                    strMember = sysUserList.get(0).getUserName();
                    for(int i = 1; i < sysUserList.size(); i ++) {
                        strMember = strMember + "," + sysUserList.get(i).getUserName();
                    }
                }
                row.createCell(2).setCellValue(strMember);
                String strRole = "";
                List<SysRole> sysRoleList = new ArrayList<>();
                userGroup.getRoles().forEach(sysRole -> {
                    sysRoleList.add(sysRole);
                });
                if(sysRoleList.size() > 0) {
                    strRole = sysRoleList.get(0).getRoleName();
                    for(int i = 1; i < sysRoleList.size(); i ++) {
                        strRole = strRole + "," + sysRoleList.get(i).getRoleName();
                    }
                }
                row.createCell(3).setCellValue(strRole);
                row.createCell(4).setCellValue(userGroup.getDataRangeCategory());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
