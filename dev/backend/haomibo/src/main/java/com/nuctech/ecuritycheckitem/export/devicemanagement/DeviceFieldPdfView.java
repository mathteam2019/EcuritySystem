/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceFieldPdfView）
 * 文件名：	DeviceFieldPdfView.java
 * 描述：	DeviceFieldPdfView
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
import com.nuctech.ecuritycheckitem.models.db.SysDevice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class DeviceFieldPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportDeviceList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysDevice> exportDeviceList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle(messageSource.getMessage("DeviceField.Title", null, currentLocale)));
            document.add(getTime());
            PdfPTable table = new PdfPTable(5);

            table.setWidthPercentage(99);
            Stream.of("DeviceField.No", "DeviceField.Device", "DeviceField.Name", "DeviceField.Category", "DeviceField.OriginalModel")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            int id = 0;

            for (SysDevice device : exportDeviceList) {
                id ++;
                addTableCell(table, String.valueOf(id));
                addTableCell(table, device.getDeviceSerial());

                addTableCell(table, device.getDeviceName());
                if(device.getCategory() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(device.getCategoryId().toString(), "DeviceCategory"));
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
                if(device.getField() != null) {
                    addTableCell(table, device.getField().getFieldDesignation());
                } else {
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
