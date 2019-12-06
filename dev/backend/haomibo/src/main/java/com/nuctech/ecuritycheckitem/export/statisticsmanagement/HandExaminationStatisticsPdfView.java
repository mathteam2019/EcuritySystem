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
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class HandExaminationStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Integer, HandExaminationResponseModel> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("判图统计"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            table.setWidthPercentage(100);
            Stream.of("序号", "时间段", "手检总量", "无查获量", "无查获率", "查获", "查获率", "手检平均时长", "手检最高时长", "手检最低时长")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });

            long index = 0;

            for (Map.Entry<Integer, HandExaminationResponseModel> entry : detailedStatistics.entrySet()) {

                HandExaminationResponseModel record = entry.getValue();
                index++;

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.getTime()));
                addTableCell(table, Long.toString(record.getTotal()));
                addTableCell(table, Long.toString(record.getNoSeizure()));
                addTableCell(table, df.format(record.getNoSeizureRate()));
                addTableCell(table, Long.toString(record.getSeizure()));
                addTableCell(table, df.format(record.getSeizureRate()));
                addTableCell(table, Double.toString(record.getAvgDuration()));
                addTableCell(table, Double.toString(record.getMaxDuration()));
                addTableCell(table, Double.toString(record.getMinDuration()));


            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
