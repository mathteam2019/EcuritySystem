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
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.Math.round;

public class PreviewStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Long, TotalStatistics> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("PreviewStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            table.setWidthPercentage(100);
            Stream.of("ID", "StatWidth", "TotalScan", "InvalidScans", "InvalidScanRate", "TotalJudge", "TotalHands", "Nosuspicion", "ScanNosuspictionRate", "NoSeizure", "NoSeizureRate", "Seizure", "SeizureRate")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.getTime()));
                addTableCell(table, Long.toString(record.getScanStatistics().getTotalScan()));
                addTableCell(table, Long.toString(record.getScanStatistics().getInvalidScan()));
                addTableCell(table, df.format(record.getScanStatistics().getInvalidScanRate()));
                addTableCell(table, Long.toString(record.getJudgeStatistics().getTotalJudge()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                addTableCell(table, Long.toString(record.getJudgeStatistics().getNoSuspictionJudge()));
                addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                addTableCell(table, df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                addTableCell(table, df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));

            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
