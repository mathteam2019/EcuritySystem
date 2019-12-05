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
            document.add(getTitle("统计预览"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            table.setWidthPercentage(100);
            Stream.of("序号", "时间段", "扫描总量", "无效扫描量", "无效率", "判图量", "手检量", "无嫌疑量", "无嫌疑率", "无查获量", "无查获率", "查获量", "查获率")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });


            long index = 0;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();
                index++;

                DecimalFormat df = new DecimalFormat("0.00");

                table.addCell(Long.toString(index));
                table.addCell(Long.toString(record.getTime()));
                table.addCell(Long.toString(record.getScanStatistics().getTotalScan()));
                table.addCell(Long.toString(record.getScanStatistics().getInvalidScan()));
                table.addCell(df.format(record.getScanStatistics().getInvalidScanRate()));
                table.addCell(Long.toString(record.getJudgeStatistics().getTotalJudge()));
                table.addCell(Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                table.addCell(Long.toString(record.getJudgeStatistics().getNoSuspictionJudge()));
                table.addCell(df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                table.addCell(Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                table.addCell(df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                table.addCell(Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                table.addCell(df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));

            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
