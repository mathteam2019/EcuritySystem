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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class JudgeStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Integer, JudgeStatisticsResponseModel> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("JudgeStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(19);

            table.setWidthPercentage(100);
            Stream.of("ID", "StatWidth", "TotalJudge", "ArtificialResult", "ArtificialResultRate", "AssignTimoutResult", "AssignTimeoutResultRate", "JudgeTimeoutResult", "JudgeTimeoutResultRate", "ScanResult", "ScanResultRate", "NoSuspicion", "NoSuspicionRate", "Suspicion", "SuspicionRate", "ArtificialJudgeDefaultTime", "ArtificialJudgeAvgTime", "ArtificialJudgeMaxTime", "ArtificialJudgeMinTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Integer, JudgeStatisticsResponseModel> entry : detailedStatistics.entrySet()) {

                JudgeStatisticsResponseModel record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.getTime()));
                addTableCell(table, Long.toString(record.getTotal()));
                addTableCell(table, Long.toString(record.getArtificialResult()));
                addTableCell(table, df.format(record.getArtificialResultRate()));
                addTableCell(table, Long.toString(record.getAssignTimeout()));
                addTableCell(table, df.format(record.getAssignTimeoutResultRate()));
                addTableCell(table, Long.toString(record.getJudgeTimeout()));
                addTableCell(table, df.format(record.getJudgeTimeoutResultRate()));
                addTableCell(table, Long.toString(record.getScanResult()));
                addTableCell(table, df.format(record.getScanResultRate()));
                addTableCell(table, Long.toString(record.getNoSuspiction()));
                addTableCell(table, df.format(record.getNoSuspictionRate()));
                addTableCell(table, Long.toString(record.getSuspiction()));
                addTableCell(table, df.format(record.getSuspictionRate()));
                addTableCell(table, Double.toString(record.getLimitedArtificialDuration()));
                addTableCell(table, Double.toString(record.getAvgArtificialJudgeDuration()));
                addTableCell(table, Double.toString(record.getMaxArtificialJudgeDuration()));
                addTableCell(table, Double.toString(record.getMinArtificialJudgeDuration()));

            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
