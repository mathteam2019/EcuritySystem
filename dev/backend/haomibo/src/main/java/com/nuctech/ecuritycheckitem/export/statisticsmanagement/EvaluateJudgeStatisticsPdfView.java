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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class EvaluateJudgeStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Integer, EvaluateJudgeResponseModel> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("评价判图"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

 table.setWidthPercentage(100);
            Stream.of("序号", "时间段", "手检总量", "误报总量", "误报率", "漏报总量", "漏报率", "手检（人工判图）量", "人工判图误报量", "人工判图误报率", "人工判图漏报量", "人工判图漏报率", "手检（智能判图）量", "智能判图误报量", "智能判图误报率", "智能判图漏报量", "智能判图漏报率")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 0;

            for (Map.Entry<Integer, EvaluateJudgeResponseModel> entry : detailedStatistics.entrySet()) {

                EvaluateJudgeResponseModel record = entry.getValue();
                index++;

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.getTime()));
                addTableCell(table, Long.toString(record.getTotal()));
                addTableCell(table, Long.toString(record.getMistakeReport()));
                addTableCell(table, df.format(record.getMistakeReportRate()));
                addTableCell(table, Long.toString(record.getMissingReport()));
                addTableCell(table, df.format(record.getMissingReportRate()));
                addTableCell(table, Long.toString(record.getArtificialJudge()));
                addTableCell(table, Long.toString(record.getArtificialJudgeMistake()));
                addTableCell(table, df.format(record.getArtificialJudgeMistakeRate()));
                addTableCell(table, Long.toString(record.getArtificialJudgeMissing()));
                addTableCell(table, df.format(record.getArtificialJudgeMissingRate()));
                addTableCell(table, Long.toString(record.getIntelligenceJudge()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMistake()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMistakeRate()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMissing()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMissingRate()));


            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
