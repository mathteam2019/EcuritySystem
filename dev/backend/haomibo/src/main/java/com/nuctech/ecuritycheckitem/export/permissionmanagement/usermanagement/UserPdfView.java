/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName UserPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class UserPdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SysUser> exportUserList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("人员列表"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            Stream.of("序号", "人员编号", "人员", "性别", "状态", "隶属机构", "账号")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });



            for (SysUser user : exportUserList) {
                table.addCell(user.getUserId().toString());
                table.addCell(user.getUserNumber());
                table.addCell(user.getUserName());
                table.addCell(user.getGender());
                table.addCell(user.getStatus());
                table.addCell(user.getOrg().getOrgName());
                table.addCell(user.getUserAccount());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
