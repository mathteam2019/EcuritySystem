/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryPdfView）
 * 文件名：	DeviceCategoryPdfView.java
 * 描述：	DeviceCategoryPdfView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceCategoryPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportCategoryList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysDeviceCategory> exportCategoryList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle("设备分类"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            Stream.of("序号", "分类编号", "分类", "生效", "上级机构编号", "上级分类", "备注")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });



            for (SysDeviceCategory category : exportCategoryList) {
                addTableCell(table, category.getCategoryId().toString());
                addTableCell(table, category.getCategoryNumber());
                addTableCell(table, category.getCategoryName());
                addTableCell(table, ConstantDictionary.getDataValue(category.getStatus()));
                if(category.getParent() != null) {
                    addTableCell(table, category.getParent().getCategoryNumber());
                    addTableCell(table, category.getParent().getCategoryName());
                } else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }
                addTableCell(table, category.getNote());

            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
