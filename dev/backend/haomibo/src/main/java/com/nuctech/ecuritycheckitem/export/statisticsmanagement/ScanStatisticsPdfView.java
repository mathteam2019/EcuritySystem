/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName OrganizationPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ScanStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Long, ScanStatistics> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("扫描统计"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(11);

            table.setWidthPercentage(100);
            Stream.of("序号", "时间段", "扫描总量", "有效扫描量", "有效率", "无效扫描量", "无效率", "通过量", "通过率", "报警量", "报警率")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Long, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index));
                addTableCell(table, Long.toString(record.getTime()));
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
