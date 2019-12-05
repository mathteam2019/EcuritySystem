/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserGroupPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
    public static InputStream buildPDFDocument(List<SysUserGroup> exportUserGroupList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("人员组授权"));
            document.add(getTime());
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(100);
            Stream.of("序号", "人员组", "组员", "角色", "数据范围")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            for (SysUserGroup userGroup : exportUserGroupList) {
                table.addCell(userGroup.getUserGroupId().toString());
                table.addCell(userGroup.getGroupName());
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
                table.addCell(strMember);
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
                table.addCell(strRole);
                table.addCell(userGroup.getDataRangeCategory());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
