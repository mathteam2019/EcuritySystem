/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（OrganizationPdfView）
 * 文件名：	OrganizationPdfView.java
 * 描述：	OrganizationPdfView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */


package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
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
import java.util.List;
import java.util.stream.Stream;

public class OrganizationPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportOrgList
     * @return
     */
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
