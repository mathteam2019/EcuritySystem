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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

public class UserOrDeviceStatisticsPdfView extends BasePdfView {

    public static InputStream buildPDFDocument(TreeMap<Long, TotalStatistics> detailedStatistics, boolean type) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            if (type) {
                document.add(getTitle("人员工时统计"));
            } else {
                document.add(getTitle("设备运行时长统计"));
            }
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            List<String> strHeaderList = new ArrayList<>();

            if (type) {
                strHeaderList = Arrays.asList(new String[]{"序号", "用户名", "扫描总量", "无效扫描量", "无效率", "判图量", "手检量", "无嫌疑量", "无嫌疑率", "无查获量", "无查获率", "查获量", "查获率"});
            }
            else {
                strHeaderList = Arrays.asList(new String[]{"序号", "设备名", "扫描总量", "无效扫描量", "无效率", "判图量", "手检量", "无嫌疑量", "无嫌疑率", "无查获量", "无查获率", "查获量", "查获率"});
            }


            table.setWidthPercentage(100);
            strHeaderList
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index++));
                addTableCell(table, record.getName());
                if (record.getScanStatistics() != null) {
                    addTableCell(table, Long.toString(record.getScanStatistics().getTotalScan()));
                    addTableCell(table, Long.toString(record.getScanStatistics().getInvalidScan()));
                    addTableCell(table, df.format(record.getScanStatistics().getInvalidScanRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                if (record.getJudgeStatistics() != null) {
                    addTableCell(table, Long.toString(record.getJudgeStatistics().getTotalJudge()));
                }
                else {
                    addTableCell(table, "无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                }
                else {
                    addTableCell(table, "无");
                }

                if (record.getJudgeStatistics() != null) {
                    addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudge()));
                    addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                    addTableCell(table, df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                    addTableCell(table, df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

            }


            document.add(table);

            document.close();

        } catch (
                DocumentException e) {
            e.printStackTrace();
        }

        return new

                ByteArrayInputStream(out.toByteArray());
    }
}
