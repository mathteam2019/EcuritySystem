/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceArchiveTemplatePdfView）
 * 文件名：	DeviceArchiveTemplatePdfView.java
 * 描述：	DeviceArchiveTemplate PdfView
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
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceArchiveTemplatePdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportTemplateList
     * @return
     */
    public static InputStream buildPDFDocument(List<SerArchiveTemplate> exportTemplateList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(getTitle(messageSource.getMessage("DeviceArchiveTemplate.Title", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(99);
            Stream.of("DeviceArchiveTemplate.No", "DeviceArchiveTemplate.Number", "DeviceArchiveTemplate.Name", "DeviceArchiveTemplate.Status", "DeviceArchiveTemplate.Category", "DeviceArchiveTemplate.Manufacturer", "DeviceArchiveTemplate.OriginalModel")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            for (SerArchiveTemplate template : exportTemplateList) {
                addTableCell(table, template.getArchivesTemplateId().toString());
                addTableCell(table, template.getArchivesTemplateNumber());
                addTableCell(table, template.getTemplateName());
                addTableCell(table, ConstantDictionary.getDataValue(template.getStatus()));
                if(template.getDeviceCategory() != null) {
                    addTableCell(table, template.getDeviceCategory().getCategoryName());
                } else {
                    addTableCell(table, "无");
                }
                addTableCell(table, template.getManufacturer());
                addTableCell(table, template.getOriginalModel());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
