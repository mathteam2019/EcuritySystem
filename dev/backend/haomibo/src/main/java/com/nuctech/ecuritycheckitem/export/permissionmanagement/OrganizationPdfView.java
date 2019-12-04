/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName OrganizationPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class OrganizationPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(List<SysOrg> exportOrgList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();


            document.add(getTitle("机构管理"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(9);

            table.setWidthPercentage(100);
            Stream.of("序号", "机构编号", "机构名称", "生效", "上级机构编号", "上级机构", "负责人", "联系方式", "备注")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            for (SysOrg org : exportOrgList) {
                table.addCell(org.getOrgId().toString());
                table.addCell(org.getOrgNumber());
                table.addCell(org.getOrgName());
                table.addCell(org.getStatus());
                if(org.getParent() != null) {
                    table.addCell(org.getParent().getOrgNumber());
                    table.addCell(org.getParent().getOrgName());
                } else {
                    table.addCell("无");
                    table.addCell("无");
                }

                table.addCell(org.getLeader());
                table.addCell(org.getMobile());
                table.addCell(org.getNote());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
