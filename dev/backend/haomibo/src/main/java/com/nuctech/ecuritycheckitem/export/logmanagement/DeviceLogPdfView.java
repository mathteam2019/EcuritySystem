/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceLogPdfView）
 * 文件名：	DeviceLogPdfView.java
 * 描述：	DeviceLogPdfView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

public class DeviceLogPdfView extends BasePdfView {

    private static Long categoryId = 0L;
    /**
     * build inputstream of data to be printed
     * @param exportLogList
     * @return
     */
    public static InputStream buildPDFDocument(List<SerDevLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            categoryId = exportLogList.get(0).getDevice().getCategoryId();
            PdfWriter.getInstance(document, out);
            document.open();
            String titleStr = "";
            if(categoryId.intValue() == Constants.SECURITY_CATEGORY_ID) {
                titleStr = messageSource.getMessage("DeviceLog.Security.Title", null, currentLocale);
            } else if(categoryId.intValue() == Constants.JUDGE_CATEGORY_ID) {
                titleStr = messageSource.getMessage("DeviceLog.Judge.Title", null, currentLocale);
            } else {
                titleStr = messageSource.getMessage("DeviceLog.Hand.Title", null, currentLocale);
            }
            document.add(getTitle(titleStr));
            document.add(getTime());
            PdfPTable table = new PdfPTable(8);

            table.setWidthPercentage(99);
            Stream.of("DeviceLog.No", "DeviceLog.Device", "DeviceLog.Account", "DeviceLog.UserName", "DeviceLog.Category", "DeviceLog.Level", "DeviceLog.Content", "DeviceLog.Time")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            int number = 0;
            for (SerDevLog log : exportLogList) {
                addTableCell(table, String.valueOf(++ number));
                if(log.getDevice() != null) {
                    addTableCell(table, log.getDevice().getDeviceName());
                    addTableCell(table, log.getDevice().getDeviceSerial());
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }
                addTableCell(table, log.getLoginName());


                addTableCell(table, ConstantDictionary.getDataValue(log.getCategory().toString(), "DeviceLogCategory"));
                addTableCell(table, ConstantDictionary.getDataValue(log.getLevel().toString(), "DeviceLogLevel"));
                addTableCell(table, log.getContent());
                addTableCell(table, formatDate(log.getTime()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
