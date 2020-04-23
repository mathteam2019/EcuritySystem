/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanStatisticsPdfView）
 * 文件名：	ScanStatisticsPdfView.java
 * 描述：	ScanStatisticsPdfView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ScanStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(List<ScanStatistics> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("ScanStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(11);

            table.setWidthPercentage(99);
            Stream.of("ID", "StatWidth", "TotalScan", "ValidScans", "ValidScanRate", "InvalidScans", "InvalidScanRate", "PassedScans", "PassedScanRate", "AlarmScans", "AlarmScanRate")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (ScanStatistics record : detailedStatistics) {


                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, record.getTime());
                addTableCell(table, Long.toString(record.getTotalScan()));
                addTableCell(table, Long.toString(record.getValidScan()));
                addTableCell(table, df.format(record.getValidScanRate()));
                addTableCell(table, Long.toString(record.getInvalidScan()));
                addTableCell(table, df.format(record.getInvalidScanRate()));
                addTableCell(table, Long.toString(record.getPassedScan()));
                addTableCell(table, df.format(record.getPassedScanRate()));
                addTableCell(table, Long.toString(record.getAlarmScan()));
                addTableCell(table, df.format(record.getAlarmScanRate()));

            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
