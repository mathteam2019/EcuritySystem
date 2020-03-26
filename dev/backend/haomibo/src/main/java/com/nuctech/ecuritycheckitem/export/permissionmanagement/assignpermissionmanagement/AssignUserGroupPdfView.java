/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignUserGroupPdfView）
 * 文件名：	AssignUserGroupPdfView.java
 * 描述：	AssignUserGroupPdfView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AssignUserGroupPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportUserGroupList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysUserGroup> exportUserGroupList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle(messageSource.getMessage("AssignUserGroup.Title", null, currentLocale)));
            document.add(getTime());
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(99);
            Stream.of("AssignUserGroup.No", "AssignUserGroup.Name", "AssignUserGroup.User", "AssignUserGroup.Role", "AssignUserGroup.Category")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            int number = 0;
            for (SysUserGroup userGroup : exportUserGroupList) {
                addTableCell(table, String.valueOf(++ number));
                addTableCell(table, userGroup.getGroupName());
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
                addTableCell(table, strMember);
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
                addTableCell(table, strRole);
                addTableCell(table, ConstantDictionary.getDataValue(userGroup.getDataRangeCategory()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
