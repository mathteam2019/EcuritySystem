/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName AssignUserPdfView.java
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
import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AssignUserPdfView {
    public static InputStream buildPDFDocument(List<SysUser> exportUserList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(7);
            Stream.of("序号",  "人员", "性别", "账号", "隶属机构", "角色", "数据范围")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });



            for (SysUser user : exportUserList) {
                table.addCell(user.getUserId().toString());
                table.addCell(user.getUserName());
                table.addCell(user.getGender());
                table.addCell(user.getUserAccount());

                List<SysDataGroup> sysGroupeList = new ArrayList<>();
                user.getDataGroups().forEach(sysDataGroup -> {
                    sysGroupeList.add(sysDataGroup);
                });

                table.addCell("");
                /*
                Todo
                Add group info
                 */

                List<SysRole> sysRoleList = new ArrayList<>();
                user.getRoles().forEach(sysRole -> {
                    sysRoleList.add(sysRole);
                });
                if(sysRoleList.size() > 0) {
                    table.addCell(sysRoleList.get(0).getRoleName());
                } else {
                    table.addCell("");
                }



                table.addCell(user.getDataRangeCategory());

            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
