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
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.HandExaminationStatisticsController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.HandExaminationResponseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class SuspictionHandgoodsStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle("判图统计"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            table.setWidthPercentage(100);

            Stream.of(SuspicionHandgoodsStatisticsController.handGoodsIDList)
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(String.valueOf(columnTitle), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.get("time")));


                for (int i = 0; i < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); i ++) {

                    addTableCell(table, record.get(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(i)).toString());

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
