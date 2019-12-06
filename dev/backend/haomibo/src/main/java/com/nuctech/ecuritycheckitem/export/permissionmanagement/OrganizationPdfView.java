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
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
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

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            for (SysOrg org : exportOrgList) {
                addTableCell(table, org.getOrgId().toString());
                addTableCell(table, org.getOrgNumber());
                addTableCell(table, org.getOrgName());
                addTableCell(table, ConstantDictionary.getDataValue(org.getStatus()));
                if(org.getParent() != null) {
                    addTableCell(table, org.getParent().getOrgNumber());
                    addTableCell(table, org.getParent().getOrgName());
                } else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                addTableCell(table, org.getLeader());
                addTableCell(table, org.getMobile());
                addTableCell(table, org.getNote());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
