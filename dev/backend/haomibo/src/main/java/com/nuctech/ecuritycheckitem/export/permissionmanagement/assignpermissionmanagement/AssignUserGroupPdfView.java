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
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class AssignUserGroupPdfView {
    public static InputStream buildPDFDocument(List<SysUserGroup> exportUserGroupList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(5);
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
                /*
                * Todo
                *  name and role
                * */
                table.addCell("");
                table.addCell("");
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
