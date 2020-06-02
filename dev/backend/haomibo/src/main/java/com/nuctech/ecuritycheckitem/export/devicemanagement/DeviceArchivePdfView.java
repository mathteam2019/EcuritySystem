/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceArchivePdfView）
 * 文件名：	DeviceArchivePdfView.java
 * 描述：	DeviceArchive PdfView
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
import com.nuctech.ecuritycheckitem.models.db.SerArchive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;


public class DeviceArchivePdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportArchiveList
     * @return
     */
    public static InputStream buildPDFDocument(List<SerArchive> exportArchiveList) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();


            document.add(getTitle(messageSource.getMessage("DeviceArchive.Title", null, currentLocale)));
            document.add(getTime());
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(99);
            Stream.of("DeviceArchive.No", "DeviceArchive.Archive", "DeviceArchive.Name", "DeviceArchive.Status", "DeviceArchive.TemplateName", "DeviceArchive.Category", "DeviceArchive.Manufacturer", "DeviceArchive.OriginalModel")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            int id = 0;
            for (SerArchive archive : exportArchiveList) {
                id ++;
                addTableCell(table, String.valueOf(id));
                addTableCell(table, archive.getArchivesNumber());
                addTableCell(table, archive.getArchivesName());
                addTableCell(table, ConstantDictionary.getDataValue(archive.getStatus()));
                if(archive.getArchiveTemplate() != null) {
                    addTableCell(table, archive.getArchiveTemplate().getTemplateName());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
                if(archive.getArchiveTemplate() != null && archive.getArchiveTemplate().getDeviceCategory() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(archive.getArchiveTemplate().getCategoryId().toString(), "DeviceCategory"));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
                if(archive.getArchiveTemplate() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(archive.getArchiveTemplate().getManufacturer()));
                    addTableCell(table, archive.getArchiveTemplate().getOriginalModel());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
